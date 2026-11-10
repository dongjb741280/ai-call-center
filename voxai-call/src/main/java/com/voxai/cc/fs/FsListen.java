package com.voxai.cc.fs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import io.netty.util.HashedWheelTimer;
import org.apache.commons.lang3.StringUtils;
import com.voxai.core.constant.Constant;
import com.voxai.core.constant.FsConstant;
import com.voxai.core.entity.RouteGetway;
import com.voxai.core.entity.Station;
import com.voxai.core.enums.ErrorCode;
import com.voxai.core.mapper.StationMapper;
import com.voxai.core.po.CallInfo;
import com.voxai.core.po.DeviceInfo;
import com.voxai.core.po.AgentInfo;
import com.voxai.cc.route.RouteSelection;
import com.voxai.cc.route.GatewayRouter;
import org.springframework.beans.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import com.voxai.cc.cache.CacheService;
import com.voxai.core.esl.internal.Context;
import com.voxai.core.esl.internal.IModEslApi;
import com.voxai.core.esl.transport.SendMsg;
import com.voxai.core.esl.transport.event.EslEvent;
import com.voxai.core.esl.transport.message.EslMessage;
import com.voxai.cc.fs.event.FsHangupCompleteEvent;
import com.voxai.cc.fs.event.base.FsBaseEvent;
import com.voxai.cc.util.RandomUtil;
import com.voxai.cc.EventType;
import com.voxai.cc.configration.Handler;
import com.voxai.cc.configration.HandlerContext;
import com.voxai.cc.exception.BusinessException;
import com.voxai.core.esl.inbound.Client;
import com.voxai.core.esl.inbound.IEslEventListener;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
public class FsListen {
    private Logger logger = LoggerFactory.getLogger(FsListen.class);

    @Value("${fs.thread.num:64}")
    private Integer threadNum;

    @Value("${spring.application.group}")
    private String group;

    @Autowired
    private StationMapper stationMapper;

    @Autowired
    protected HashedWheelTimer hashedWheelTimer;

    @Value("${audio.codecs:OPUS,PCMU,PCMA}")
    private String codecs;

    @Value("${server.port}")
    private Integer localPort;

    private String localAddress;


    private ScheduledExecutorService checkFsThread = new ScheduledThreadPoolExecutor(1, new ThreadFactoryBuilder().setNameFormat("check-freeswitch-pool-%d").build());

    /**
     * 媒体集合
     */
    private Map<String, Client> fsClient = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * 线程组
     */
    private Map<Integer, ThreadPoolExecutor> executorMap = new ConcurrentHashMap<>();

    private final Map<String, PendingOriginate> pendingRoutes = new ConcurrentHashMap<>();

    private static final class PendingOriginate {
        final String media, display, called;
        final RouteSelection route;
        final Integer timeout, originateTimeout;
        final boolean autoAnswer;
        final Map<String, String> variables;
        final String[] headers;
        volatile boolean progressed;
        PendingOriginate(String media, RouteSelection route, String display, String called, Integer timeout,
                         Integer originateTimeout, boolean autoAnswer, Map<String, String> variables, String[] headers) {
            this.media = media; this.route = route; this.display = display; this.called = called;
            this.timeout = timeout; this.originateTimeout = originateTimeout; this.autoAnswer = autoAnswer;
            this.variables = new HashMap<>(variables); this.headers = headers == null ? new String[0] : headers.clone();
        }
    }


    @Autowired
    private HandlerContext handlerContext;

    @Autowired
    private CacheService cacheService;


    public void start() {
        for (int i = 0; i < threadNum; i++) {
            ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("fs-pool-" + i).build();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
            executorMap.put(i, executor);
        }

        Map<String, Object> params = new HashMap<>();
        params.put("applicationType", 4);
        params.put("applicationGroup", group);
        List<Station> fsStations = stationMapper.selectListByMap(params);
        for (Station station : fsStations) {
            if (station.getStatus() == 1) {
                connect(station.getApplicationHost(), station.getApplicationPort(), station.getPwd());
            }
        }

        checkFsThread.scheduleAtFixedRate(() -> {
            try {
                checkConnect();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }, 2, 1, TimeUnit.MINUTES);
    }

    /**
     *
     */
    private void checkConnect() {
        Map<String, Object> params = new HashMap<>();
        params.put("applicationType", 4);
        params.put("applicationGroup", group);
        List<Station> fsStations = stationMapper.selectListByMap(params);

        for (Station station : fsStations) {
            Client client = fsClient.get(station.getHost());
            try {
                if (station.getStatus() != 1 && client == null) {
                    continue;
                }

                if (station.getStatus() == 1 && client == null) {
                    connect(station.getApplicationHost(), station.getApplicationPort(), station.getPwd());
                    continue;
                }

                //断开连接，fs下线
                if (station.getStatus() != 1 && client != null) {
                    logger.info("fs :{} off line", station.getHost());
                    fsClient.remove(station.getHost());
                    client.close();
                    continue;
                }

                //失败重连
                if (!client.isActivate()) {
                    logger.info("freeswitch {}:{} is close", station.getApplicationHost(), station.getApplicationPort());
                    client.close();
                    connect(station.getApplicationHost(), station.getApplicationPort(), station.getPwd());
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 连接freeswitch
     *
     * @param host
     * @param port
     * @param password
     * @return
     */
    private void connect(String host, Integer port, String password) {
        Client client = new Client();
        try {
            SocketAddress socketAddress = new InetSocketAddress(host, port);
            logger.info("Connecting to {} passwd:{}", socketAddress, password);
            client.connect(socketAddress, password, 3);
            if (localAddress == null) {
                InetSocketAddress address = (InetSocketAddress) client.getChannel().localAddress();
                localAddress = Constant.HTTP + address.getAddress().getHostAddress() + Constant.CO + localPort + Constant.SK + Constant.FS_API;
                //cacheService.setHost(localAddress);
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            return;
        }
        fsClient.put(host + ":" + port, client);
        client.setEventSubscriptions(IModEslApi.EventFormat.PLAIN, "all");
        IEslEventListener listener = new IEslEventListener() {
            @Override
            public void onEslEvent(Context ctx, EslEvent event) {
                String eventName = event.getEventName();
                if ("CHANNEL_PROGRESS".equals(eventName) || "CHANNEL_PROGRESS_MEDIA".equals(eventName)) {
                    PendingOriginate pending = pendingRoutes.get(event.getEventHeaders().get("Unique-ID"));
                    if (pending != null) pending.progressed = true;
                } else if ("CHANNEL_CALLSTATE".equals(eventName)
                        && "RINGING".equalsIgnoreCase(event.getEventHeaders().get("Channel-Call-State"))) {
                    PendingOriginate pending = pendingRoutes.get(event.getEventHeaders().get("Unique-ID"));
                    if (pending != null) pending.progressed = true;
                }
                /**
                 * https://freeswitch.org/confluence/display/FREESWITCH/Event+List
                 */
                switch (event.getEventName()) {
                    case FsConstant.RE_SCHEDULE:
                        return;
                    case FsConstant.API:
                        return;
                    case FsConstant.HEARTBEAT:
                        return;
                    case FsConstant.RECV_RTCP_MESSAGE:
                        return;
                    case FsConstant.CODEC:
                        return;
                    case FsConstant.DTMF_SEND:
                        return;
                    case FsConstant.RECV_INFO:
                        return;
                    case FsConstant.CHANNEL_STATE:
                        return;
                    case FsConstant.DEL_SCHEDULE:
                        return;
                    case FsConstant.CHANNEL_CREATE:
                        break;
                    case FsConstant.CHANNEL_ORIGINATE:
                        break;
                    case FsConstant.CHANNEL_PROGRESS:
                        break;
                    case FsConstant.PRESENCE_IN:
                        break;
                    case FsConstant.CHANNEL_CALLSTATE:
                        break;
                    case FsConstant.CALL_UPDATE:
                        break;
                    case FsConstant.DETECTED_SPEECH:
                        break;
                    case FsConstant.CHANNEL_EXECUTE:
                        break;
                    case FsConstant.CHANNEL_PARK:
                        break;
                    case FsConstant.CHANNEL_UNPARK:
                        break;
                    case FsConstant.PRIVATE_COMMAND:
                        break;
                    case FsConstant.CHANNEL_EXECUTE_COMPLETE:
                        break;
                    case FsConstant.CHANNEL_HANGUP:
                        break;
                    case FsConstant.CHANNEL_HANGUP_COMPLETE:
                        break;
                    case FsConstant.CHANNEL_OUTGOING:
                        break;
                    case FsConstant.CHANNEL_ANSWER:
                        break;
                    case FsConstant.CHANNEL_DESTROY:
                        break;
                    case FsConstant.CHANNEL_BRIDGE:
                        break;
                    case FsConstant.RECORD_START:
                        break;
                    case FsConstant.MEDIA_BUG_START:
                        break;
                    case FsConstant.MEDIA_BUG_STOP:
                        break;
                    case FsConstant.PLAYBACK_START:
                        break;
                    case FsConstant.PLAYBACK_STOP:
                        break;
                    case FsConstant.CHANNEL_UNBRIDGE:
                        break;
                    case FsConstant.DTMF:
                        break;
                    case FsConstant.CHANNEL_PROGRESS_MEDIA:
                        break;
                    case FsConstant.RECORD_STOP:
                        break;
                    case FsConstant.CUSTOM:
                        break;
                    case FsConstant.CHANNEL_HOLD:
                        break;
                    case FsConstant.CHANNEL_UNHOLD:
                        break;
                    case FsConstant.RING_ASR:
                        break;
                    case FsConstant.RELOADXML:
                        break;
                    case FsConstant.ADD_SCHEDULE:
                        break;
                    default:
                        logger.info("event:{}, hander:{}", event.getEventName(), JSON.toJSONString(event.getEventHeaders()));
                        return;
                }

                FsBaseEvent formatEvent = formatEvent(ctx, event, eventName);
                if (formatEvent == null) {
                    return;
                }
                formatEvent.setRemoteAddress(host + Constant.CO + port);
                formatEvent.setLocalAddress(localAddress);

                /**
                 * 一个callId挂机处理必须使用一个相同的线程
                 */
                ExecutorService executorService = null;
                // Restore the local device index from Redis after restart, using the trusted ESL channel variable.
                CallInfo indexedCall = cacheService.getCallInfo(formatEvent.getDeviceId());
                if (indexedCall == null && formatEvent.getCallId() != null) {
                    indexedCall = cacheService.getCallInfo(formatEvent.getCallId());
                    if (indexedCall != null && indexedCall.getDeviceInfoMap().containsKey(formatEvent.getDeviceId())) {
                        cacheService.addDevice(formatEvent.getDeviceId(), indexedCall.getCallId());
                    } else { indexedCall = null; }
                }
                if (indexedCall != null) {
                    executorService = executorMap.get(RandomUtil.getNum(indexedCall.getCallId().toString(), threadNum));
                }
                if (executorService == null && formatEvent instanceof FsHangupCompleteEvent) {
                    CallInfo callInfo = cacheService.getCallInfo(formatEvent.getDeviceId());
                    if (callInfo == null) {
                        return;
                    }
                    executorService = executorMap.get(RandomUtil.getNum(callInfo.getCallId().toString(), threadNum));
                }
                if (executorService == null) {
                    executorService = executorMap.get(RandomUtil.getNum(formatEvent.getDeviceId(), threadNum));
                }
                executorService.execute(() -> {
                    try {
                        if (logger.isDebugEnabled()) {
                            logger.info("ctx:{}, event:{}", ctx, event);
                        }
                        Handler handler = handlerContext.getInstance(formatEvent.getEventName());
                        if (handler != null) {
                            handler.handleEvent(formatEvent);
                        }
                    } catch (Exception e) {
                        logger.error("deviceId:{} eventName:{} errorMessage:{}", event.getEventHeaders().get("Unique-ID"), eventName, e.getMessage(), e);
                    }
                });
            }

            @Override
            public void onClose(Channel channel) {
                logger.warn("channel close {}", channel);
                client.close();
            }
        };

        client.addEventListener(listener);
        logger.info("connect {}:{} success", host, port);
    }

    /**
     * 消息转换
     *
     * @param context
     * @param eslEvent
     * @param eventName
     * @return
     */
    private FsBaseEvent formatEvent(Context context, EslEvent eslEvent, String eventName) {
        try {
            Class clzss = EventType.getClassByCmd(eventName);
            if (clzss == null) {
                return null;
            }
            Map map = eslEvent.getEventHeaders();
            JSONObject json = new JSONObject(map);
            if (StringUtils.isBlank(eventName)) {
                return null;
            }
            FsBaseEvent event = (FsBaseEvent) JSON.toJavaObject(json, clzss);
            event.setEventName(eventName);
            event.setContext(context);
            return event;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     *
     */
    public void stop() {
        if (!fsClient.isEmpty()) {
            logger.info("freeswitch client stop");
            fsClient.forEach((i, client) -> {
                if (client.isActivate()) {
                    client.close();
                }
            });
        }
        checkFsThread.shutdown();
        executorMap.forEach((i, executor) -> {
            executor.shutdown();
        });
        hashedWheelTimer.stop();
    }

    /**
     * @param sendMsg
     */
    public void sendMessage(String media, SendMsg sendMsg) {
        if (StringUtils.isBlank(media)) {
            logger.error("send to media is null, sendMsg:{}", sendMsg);
            return;
        }
        fsClient.get(media).sendMessage(sendMsg);
    }


    /**
     * 发起呼叫
     *
     * @param routeGetway
     * @param display
     * @param called
     * @param callId
     * @param deviceId
     * @param timeout
     * @param originateTimeout
     * @param sipHeaders
     */
    public void makeCall(RouteGetway routeGetway, String display, String called, Long callId, String deviceId, Integer timeout, Integer originateTimeout, String... sipHeaders) {
        makeCall(routeGetway, display, called, callId, deviceId, timeout, originateTimeout, false, sipHeaders);
    }

    /**
     * 发起呼叫（支持自动应答，用于班长监控场景）
     */
    public void makeCall(RouteGetway routeGetway, String display, String called, Long callId, String deviceId, Integer timeout, Integer originateTimeout, boolean autoAnswer, String... sipHeaders) {
        String media = RandomUtil.getRandomKey(fsClient.keySet());
        if (StringUtils.isBlank(media)) {
            throw new BusinessException(ErrorCode.MEDIA_NOT_AVALIABLE);
        }
        makeCall(media, routeGetway, display, called, callId, deviceId, timeout, originateTimeout, autoAnswer, sipHeaders);
    }

    /**
     * 发起呼叫
     * <p>
     * bgapi originate {return_ring_ready=true,sip_contact_user=01017818388,ring_asr=true,absolute_codec_string=^^:PCMU:PCMA,origination_caller_id_number=01017718388,origination_caller_id_name=01017718388,origination_uuid=192.168.1.1-25-5f8fe34b-1bb-76,sip_auto_answer=true}sofia/external/8731279@192.168.1.1:7470 &park()
     *
     * @param media
     * @param routeGetway      网关
     * @param display          主叫
     * @param called           被叫
     * @param deviceId
     * @param originateTimeout
     * @param sipHeaders
     */
    public void makeCall(String media, RouteGetway routeGetway, String display, String called, Long callId, String deviceId, Integer timeout, Integer originateTimeout, String... sipHeaders) {
        makeCall(media, routeGetway, display, called, callId, deviceId, timeout, originateTimeout, false, sipHeaders);
    }

    public void makeCall(String media, RouteGetway routeGetway, String display, String called, Long callId, String deviceId, Integer timeout, Integer originateTimeout, boolean autoAnswer, String... sipHeaders) {
        originate(media, routeGetway, display, called, callId, deviceId, timeout, originateTimeout, autoAnswer, java.util.Collections.emptyMap(), sipHeaders);
    }

    public String availableMedia() {
        return fsClient.entrySet().stream().filter(e -> e.getValue().isActivate()).map(Map.Entry::getKey).sorted().findFirst().orElse(null);
    }

    /** Reconciliation must observe all already-queued task events before rebuilding a final CDR. */
    public void withTaskEvents(Long callId, Runnable operation) {
        ExecutorService executor=executorMap.get(RandomUtil.getNum(callId.toString(),threadNum));
        if (executor==null) { throw new IllegalStateException("Media event executor unavailable"); }
        try {
            executor.submit(operation).get(10,TimeUnit.SECONDS);
        } catch (InterruptedException failure) {
            Thread.currentThread().interrupt(); throw new IllegalStateException("Task reconciliation interrupted",failure);
        } catch (java.util.concurrent.ExecutionException failure) {
            if (failure.getCause() instanceof RuntimeException) { throw (RuntimeException)failure.getCause(); }
            throw new IllegalStateException("Task reconciliation failed",failure.getCause());
        } catch (java.util.concurrent.TimeoutException failure) {
            // The queued operation still owns its database fence; an HTTP timeout never starts a second call.
            throw new IllegalStateException("Task reconciliation deferred",failure);
        }
    }

    private String taskApi(String media, String command, String args) {
        Client client = fsClient.get(media);
        if (client == null || !client.isActivate()) { throw new IllegalStateException("Task media connection unavailable"); }
        EslMessage response = client.sendApiCommand(command, args);
        if (response == null) { throw new IllegalStateException("Empty media response"); }
        return String.join("\n", response.getBodyLines()).trim();
    }

    public boolean taskChannelExists(String media, String uuid) {
        if (uuid == null || !uuid.matches("[A-Za-z0-9_-]{1,64}")) { throw new IllegalArgumentException("Invalid channel ID"); }
        String result = taskApi(media, "uuid_exists", uuid);
        if (!"true".equals(result) && !"false".equals(result)) { throw new IllegalStateException("Uncertain media response"); }
        return "true".equals(result);
    }

    public java.util.List<String> taskChannels(String media, Long callId) {
        com.alibaba.fastjson.JSONObject channels = com.alibaba.fastjson.JSON.parseObject(taskApi(media, "show", "channels as json"));
        java.util.List<String> found = new java.util.ArrayList<>();
        if (channels == null || !channels.containsKey("row_count")) { throw new IllegalStateException("Invalid channel inventory"); }
        com.alibaba.fastjson.JSONArray rows = channels.getJSONArray("rows");
        if (rows == null && channels.getIntValue("row_count") != 0) { throw new IllegalStateException("Incomplete channel inventory"); }
        if (rows != null) {
            for (Object item : rows) {
                String uuid = ((com.alibaba.fastjson.JSONObject) item).getString("uuid");
                if (uuid == null || !uuid.matches("[A-Za-z0-9_-]{1,64}")) { throw new IllegalStateException("Invalid channel inventory"); }
                if (String.valueOf(callId).equals(taskApi(media, "uuid_getvar", uuid + " callId"))) { found.add(uuid); }
            }
        }
        return found;
    }

    public java.util.concurrent.CompletableFuture<EslEvent> originateTask(String media, RouteGetway route, String display,
            String called, Long callId, String uuid, int timeout, int maxCallSeconds) {
        Map<String,String> variables = new HashMap<>();
        variables.put("hangup_after_bridge", "true");
        variables.put("execute_on_answer", "sched_hangup +" + maxCallSeconds + " NORMAL_CLEARING");
        return originate(media, route, display, called, callId, uuid, timeout, timeout, false, variables);
    }

    private java.util.concurrent.CompletableFuture<EslEvent> originate(String media, RouteGetway routeGetway, String display,
            String called, Long callId, String deviceId, Integer timeout, Integer originateTimeout, boolean autoAnswer,
            Map<String,String> variables, String... sipHeaders) {
        Client client = null;
        if (StringUtils.isBlank(media)) {
            media = RandomUtil.getRandomKey(fsClient.keySet());
        }
        if (StringUtils.isBlank(media)) {
            throw new BusinessException(ErrorCode.MEDIA_NOT_AVALIABLE);
        }
        logger.info("callId:{} makeCall routeGetway:{} display:{} called:{}, timeout{}, originateTimeout:{}", callId, routeGetway.getName(), display, called, timeout, originateTimeout);
        if (StringUtils.isBlank(media)) {
            throw new BusinessException(ErrorCode.MEDIA_NOT_AVALIABLE);
        }
        client = fsClient.get(media);

        if (client == null || !client.isActivate()) {
            throw new BusinessException(ErrorCode.MEDIA_NOT_AVALIABLE);
        }
        final String originateMedia = media;
        if (routeGetway instanceof RouteSelection) {
            RouteSelection selection = (RouteSelection) routeGetway;
            PendingOriginate pending = new PendingOriginate(media, selection, display, called, timeout,
                    originateTimeout, autoAnswer, variables, sipHeaders);
            // Apply the route rule before the gateway prefixes. Keep originals for any next gateway.
            display = selection.caller(display);
            called = selection.called(called);
            pendingRoutes.put(deviceId, pending);
        }

        if (StringUtils.isNotBlank(routeGetway.getMediaHost())) {
            called = called + Constant.AT + routeGetway.getMediaHost() + Constant.CO + routeGetway.getMediaPort();
        }
        if (StringUtils.isNotBlank(routeGetway.getCallerPrefix())) {
            display = routeGetway.getCallerPrefix() + display;
        }
        if (StringUtils.isNotBlank(routeGetway.getCalledPrefix())) {
            called = routeGetway.getCalledPrefix() + called;
        }
        StringBuffer sipBuffer = new StringBuffer();
        if (sipHeaders != null) {
            for (int i = 0; i < sipHeaders.length; i++) {
                sipBuffer.append(FsConstant.SIP_HEADER + sipHeaders[i]);
                if (i < sipHeaders.length - 1) {
                    sipBuffer.append(";");
                }
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put("callId", callId);
        params.put("deviceId", deviceId);
        params.put("caller", display);
        params.put("called", called);
        if (StringUtils.isNoneBlank(routeGetway.getSipHeader1())) {
            if (StringUtils.isNoneBlank(sipBuffer)) {
                sipBuffer.append(";");
            }
            String sipHeader1 = routeGetway.getSipHeader1();
            sipHeader1 = expression(sipHeader1, params);
            sipBuffer.append(FsConstant.SIP_HEADER + sipHeader1);
        }
        if (StringUtils.isNoneBlank(routeGetway.getSipHeader2())) {
            if (StringUtils.isNoneBlank(sipBuffer)) {
                sipBuffer.append(";");
            }
            String sipHeader2 = routeGetway.getSipHeader2();
            sipHeader2 = expression(sipHeader2, params);
            sipBuffer.append(FsConstant.SIP_HEADER + sipHeader2);
        }
        if (StringUtils.isNoneBlank(routeGetway.getSipHeader3())) {
            if (StringUtils.isNoneBlank(sipBuffer)) {
                sipBuffer.append(";");
            }
            sipBuffer.append(FsConstant.SIP_HEADER + routeGetway.getSipHeader3());
        }
        // originate 的 {} 变量用 ^^; 转义为分号分隔，使 absolute_codec_string 能携带逗号（如 OPUS,PCMU,PCMA）
        StringBuilder builder = new StringBuilder();
        builder.append("{^^;return_ring_ready=true").append(";").append("sip_contact_user=").append(display).append(";").append("ring_asr=true").append(";").append("absolute_codec_string=").append(codecs).append(";").append("origination_caller_id_number=").append(display).append(";").append("origination_caller_id_name=").append(display).append(";").append("origination_uuid=").append(deviceId);
        builder.append(";callId=").append(callId);
        variables.forEach((key, value) -> builder.append(";").append(key).append("=").append(value));
        if (autoAnswer) {
            builder.append(";").append("sip_auto_answer=true");
        }
        Integer effectiveTimeout = originateTimeout != null ? originateTimeout : timeout;
        if (effectiveTimeout != null) {
            builder.append(";").append("originate_timeout=" + effectiveTimeout);
        }
        if (StringUtils.isNoneBlank(sipBuffer)) {
            builder.append(";").append(sipBuffer);
        }
        if (StringUtils.isNotBlank(routeGetway.getMediaHost())) {
            builder.append("}").append(FsConstant.SOFIA + Constant.SK + routeGetway.getProfile() + Constant.SK).append(called).append(FsConstant.PARK);
        } else {
            builder.append("}user/").append(called).append(FsConstant.PARK);
        }
        java.util.concurrent.CompletableFuture<EslEvent> job;
        try {
            job = client.sendBackgroundApiCommand(FsConstant.ORIGINATE, builder.toString());
        } catch (RuntimeException failure) {
            if (!(routeGetway instanceof RouteSelection)) throw failure;
            job = new java.util.concurrent.CompletableFuture<>();
            job.completeExceptionally(failure);
        }
        if (routeGetway instanceof RouteSelection) {
            job.whenComplete((event, error) -> {
                String body = event == null ? "" : String.join("\n", event.getEventBodyLines());
                if (error == null && (body == null || !body.trim().startsWith("-ERR"))) return;
                CallInfo call = cacheService.getCallInfo(callId);
                if (call == null || call.getTaskId() != null || !pendingRoutes.containsKey(deviceId)) return;
                String cause = error != null ? "NETWORK_OUT_OF_ORDER" : originateFailureCause(body);
                ThreadPoolExecutor executor = executorMap.get(RandomUtil.getNum(callId.toString(), threadNum));
                if (executor == null) return;
                executor.execute(() -> {
                    try {
                        com.voxai.cc.configration.Handler handler = handlerContext.getInstance(FsConstant.CHANNEL_HANGUP_COMPLETE);
                        if (handler instanceof com.voxai.cc.fs.handler.FsHangupCompleteHandler) {
                            com.voxai.cc.fs.event.FsHangupCompleteEvent failure = new com.voxai.cc.fs.event.FsHangupCompleteEvent();
                            failure.setEventName(FsConstant.CHANNEL_HANGUP_COMPLETE);
                            failure.setCallId(callId);
                            failure.setDeviceId(deviceId);
                            failure.setTimestamp(System.currentTimeMillis() * 1000);
                            failure.setHangupCause(cause);
                            failure.setRemoteAddress(originateMedia);
                            ((com.voxai.cc.fs.handler.FsHangupCompleteHandler) handler).handleEvent(failure);
                        }
                    } catch (Exception failure) {
                        logger.error("callId:{} originate failure handling failed", callId, failure);
                    }
                });
            });
        }
        return job;
    }

    private String originateFailureCause(String body) {
        String result = body == null ? "" : body.toUpperCase(java.util.Locale.ROOT);
        if (result.contains("USER_BUSY")) return "USER_BUSY";
        if (result.contains("NO_ANSWER")) return "NO_ANSWER";
        if (result.contains("CALL_REJECTED") || result.contains("FORBIDDEN")) return "CALL_REJECTED";
        if (result.contains("NO_ROUTE") || result.contains("INVALID_GATEWAY")) return "DESTINATION_OUT_OF_ORDER";
        if (result.contains("TEMPORARY")) return "NORMAL_TEMPORARY_FAILURE";
        if (result.contains("CONGESTION")) return "SWITCH_CONGESTION";
        return "INVALID_PROFILE";
    }

    public void routeAnswered(String deviceId) {
        PendingOriginate pending = pendingRoutes.remove(deviceId);
        if (pending != null) pending.route.succeeded();
    }

    /** Called after persisting the failed device, before ending the overall call. */
    public boolean failover(CallInfo call, DeviceInfo failed) {
        PendingOriginate pending = pendingRoutes.remove(failed.getDeviceId());
        if (pending == null || failed.getAnswerTime() != null && failed.getAnswerTime() > 0) return false;
        pending.route.failed(failed.getHangupCause());
        // Task attempts own a durable customer UUID and are reconciled by the dialer, never re-originated here.
        if (call.getTaskId() != null || call.getEndTime() != null || !GatewayRouter.isGatewayFailure(failed.getHangupCause())
                || pending.progressed) return false;
        RouteSelection next = pending.route.next();
        if (next == null) return false;

        DeviceInfo replacement = new DeviceInfo();
        BeanUtils.copyProperties(failed, replacement);
        String previous = failed.getDeviceId(), uuid = java.util.UUID.randomUUID().toString();
        replacement.setDeviceId(uuid); replacement.setCallTime(System.currentTimeMillis());
        replacement.setEndTime(null); replacement.setTalkTime(null); replacement.setHangupCause(null);
        replacement.setSipStatus(null); replacement.setSipProtocol(null); replacement.setChannelName(null);
        call.getDeviceList().remove(previous);
        call.getDeviceList().add(uuid);
        call.getDeviceInfoMap().put(uuid, replacement);
        call.getNextCommands().forEach(command -> {
            if (previous.equals(command.getDeviceId())) command.setDeviceId(uuid);
            if (previous.equals(command.getNextValue())) command.setNextValue(uuid);
        });
        if (failed.getAgentKey() != null) {
            AgentInfo agent = cacheService.getAgentInfo(failed.getAgentKey());
            if (agent != null && previous.equals(agent.getDeviceId()) && java.util.Objects.equals(call.getCallId(), agent.getCallId())) {
                agent.setDeviceId(uuid); cacheService.addAgentInfo(agent);
            }
        }
        cacheService.addDevice(uuid, call.getCallId());
        cacheService.addCallInfo(call);
        logger.info("callId:{} gateway:{} failed, retry gateway:{} device:{}", call.getCallId(), pending.route.getId(), next.getId(), uuid);
        originate(pending.media, next, pending.display, pending.called, call.getCallId(), uuid,
                pending.timeout, pending.originateTimeout, pending.autoAnswer, pending.variables, pending.headers);
        return true;
    }


    /**
     * 桥接电话
     *
     * @param media
     * @param callId
     * @param deviceId1
     * @param deviceId2
     */
    public void bridgeCall(String media, Long callId, String deviceId1, String deviceId2) {
        // 桥接前锁定编解码，避免 G.729 passthrough 模式导致转码失败
        this.sendArgs(media, deviceId1, FsConstant.SET, "absolute_codec_string=" + codecs);
        this.sendArgs(media, deviceId2, FsConstant.SET, "absolute_codec_string=" + codecs);
        this.sendArgs(media, deviceId1, FsConstant.SET, FsConstant.PARK_AFTER_BRIDGE);
        this.sendArgs(media, deviceId1, FsConstant.SET, FsConstant.HANGUP_AFTER_BRIDGE);
        this.sendArgs(media, deviceId2, FsConstant.SET, FsConstant.HANGUP_AFTER_BRIDGE);
        this.sendArgs(media, deviceId2, FsConstant.SET, FsConstant.PARK_AFTER_BRIDGE);
        StringBuilder builder = new StringBuilder();
        builder.append(deviceId1).append(FsConstant.SPACE).append(deviceId2);
        sendBgapiMessage(media, FsConstant.BRIDGE, builder.toString());
    }

    /**
     * @param media
     * @param from
     * @param to
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void transferCall(String media, String from, String to) {
        StringBuilder builder = new StringBuilder();
        builder.append(from).append("  -both 'set:hangup_after_bridge=false,set:park_after_bridge=true,park:' inline ");
        fsClient.get(media).sendBackgroundApiCommand(FsConstant.TRANSFER, builder.toString());
    }


    /**
     * 不使用线程池发送
     */
    public void sendSyncMessage(String media, SendMsg sendMsg) {
        fsClient.get(media).sendMessage(sendMsg);
    }

    /**
     * @param media
     * @param cmd
     * @param args
     */
    public void sendBgapiMessage(String media, String cmd, String args) {
        fsClient.get(media).sendBackgroundApiCommand(cmd, args);
    }


    /**
     * 应答
     *
     * @param media
     * @param deviceId
     * @return
     */
    public EslMessage answer(String media, String deviceId) {
        return fsClient.get(media).sendApiCommand("uuid_phone_event", deviceId + " talk");
    }

    /**
     * 挂机
     *
     * @param media
     * @param callId
     * @param deviceId
     */
    public void hangupCall(String media, Long callId, String deviceId) {
        if (StringUtils.isBlank(media) || StringUtils.isBlank(deviceId)) {
            logger.warn("media:{} or deviceId:{} is null", media, deviceId);
            return;
        }
        SendMsg hangupMsg = new SendMsg(deviceId);
        hangupMsg.addCallCommand(FsConstant.EXECUTE);
        hangupMsg.addExecuteAppName(FsConstant.HANGUP);
        hangupMsg.addExecuteAppArg(FsConstant.NORMAL_CLEARING);
        logger.info("hangup call:{}, device:{}", callId, deviceId);
        this.sendMessage(media, hangupMsg);
    }

    /**
     * 挂机
     *
     * @param media
     * @param callId
     * @param deviceId
     */
    public void killCall(String media, Long callId, String deviceId) {
        if (StringUtils.isBlank(media) || StringUtils.isBlank(deviceId)) {
            logger.warn("media:{} or deviceId:{} is null", media, deviceId);
            return;
        }
        logger.info("hangup call:{}, device:{}", callId, deviceId);
        fsClient.get(media).sendBackgroundApiCommand("uuid_kill ", deviceId);
    }

    /**
     * update call
     *
     * @param media
     * @param deviceId
     * @param name
     * @param arg
     */
    public void sendArgs(String media, String deviceId, String name, String arg) {
        SendMsg msg = new SendMsg(deviceId);
        msg.addCallCommand(FsConstant.EXECUTE);
        msg.addExecuteAppName(name);
        msg.addExecuteAppArg(arg);
        msg.addAsync();
        this.sendMessage(media, msg);
    }


    /**
     * bgapi uuid_transfer  sswitch-301-60f4c396-59-85 -both 'set:hangup_after_bridge=false,set:park_after_bridge=true,park:' inline
     * <p>
     * 从bridge 拆线
     *
     * @param mediaHost
     * @param deviceId
     */
    public void bridgeBreak(String mediaHost, String deviceId) {
        StringBuilder builder = new StringBuilder();
        builder.append(deviceId).append("  -both 'set:hangup_after_bridge=false,set:park_after_bridge=true,park:' inline ");
        fsClient.get(mediaHost).sendBackgroundApiCommand(FsConstant.TRANSFER, builder.toString());
    }

    /**
     * 保持放音
     *
     * @param media
     * @param deviceId
     * @param play
     */
    public void holdPlay(String media, String deviceId, String play) {
        this.sendArgs(media, deviceId, FsConstant.SET, FsConstant.PLAYBACK_TERMINATORS);
        this.sendArgs(media, deviceId, FsConstant.SET, FsConstant.PLAYBACK_DELIMITER);
        this.sendArgs(media, deviceId, FsConstant.SET, FsConstant.TTS_ENGINE);
        this.sendArgs(media, deviceId, FsConstant.PLAYBACK, play);
    }

    /**
     * 放音
     *
     * @param media
     * @param deviceId
     * @param file
     */
    public void playfile(String media, String deviceId, String file) {
        SendMsg playfile = new SendMsg(deviceId);
        playfile.addCallCommand(FsConstant.EXECUTE);
        playfile.addExecuteAppName(FsConstant.PLAYBACK);
        playfile.addExecuteAppArg(file);
        playfile.addAsync();
        this.sendMessage(media, playfile);
    }


    /**
     * 加入会议
     *
     * @param media
     * @param deviceId
     * @param conferenceName
     */
    public void conference(String media, String deviceId, String conferenceName) {
        this.sendArgs(media, deviceId, FsConstant.CONFERENCE, conferenceName);
    }

    /**
     * 停止放音
     *
     * @param media
     * @param deviceId
     */
    public void playBreak(String media, String deviceId) {
        SendMsg playback = new SendMsg(deviceId);
        playback.addCallCommand(FsConstant.EXECUTE);
        playback.addExecuteAppName(FsConstant.BREAK_);
        this.sendMessage(media, playback);
    }


    /**
     * 表达式替换
     *
     * @param body   #{[name]}
     * @param params
     */
    protected String expression(String body, Map<String, Object> params) {
        ExpressionParser parser = new SpelExpressionParser();
        TemplateParserContext parserContext = new TemplateParserContext();
        String content = null;
        try {
            content = parser.parseExpression(body, parserContext).getValue(params, String.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return content;
    }
}
