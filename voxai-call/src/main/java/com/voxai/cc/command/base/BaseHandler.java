package com.voxai.cc.command.base;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import com.voxai.core.entity.Playback;
import com.voxai.core.po.CallInfo;
import com.voxai.core.po.DeviceInfo;
import com.voxai.core.po.GroupInfo;
import com.voxai.core.po.NextCommand;
import com.voxai.core.po.VdnSchedulePo;
import com.voxai.core.util.SnowflakeIdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;
import com.voxai.cc.cache.CacheService;
import com.voxai.cc.command.*;
import com.voxai.cc.fs.FsListen;
import com.voxai.cc.service.AgentService;
import com.voxai.cc.service.CallCdrService;
import com.voxai.cc.service.GroupMemoryService;
import com.voxai.cc.websocket.WebSocketHandler;
import com.voxai.core.esl.transport.SendMsg;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class BaseHandler {
    protected Logger logger = LoggerFactory.getLogger(BaseHandler.class);

    /**
     * 当前按键导航配置id（cc_vdn_config.id），收键后据此匹配路由
     */
    protected static final String DTMF_NAVIGATE_KEY = "dtmfNavigateId";

    @Value("${record.path:/Users/dongjb/soft/freeswitch/}")
    protected String recordPath;

    @Autowired
    protected FsListen fsListen;

    @Autowired
    protected CacheService cacheService;

    @Autowired
    protected GroupHandler groupHandler;

    @Autowired
    protected TransferIvrHandler transferIvrHandler;

    @Autowired
    protected TransferCallHandler transferCallHandler;

    @Autowired
    protected TransferAgentHandler transferAgentHandler;

    @Autowired
    protected VdnHandler vdnHandler;

    @Autowired
    protected CallCdrService callCdrService;

    @Autowired
    protected WebSocketHandler webSocketHandler;

    @Autowired
    protected AgentService agentService;

    @Autowired
    protected GroupMemoryService groupMemoryService;

    @Autowired
    protected OverFlowHandler overFlowHandler;

    @Autowired
    protected SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    protected RedisTemplate redisTemplate;

    @Autowired
    protected RestTemplate httpClient;

    /**
     * 被叫挂机时，需要把主叫挂机，主叫挂机时不需要单独挂被叫
     *
     * @param callId
     * @param deviceId
     */
    protected void hangupCall(String media, Long callId, String deviceId) {
        fsListen.hangupCall(media, callId, deviceId);
    }

    /**
     * 放音
     *
     * @param media
     * @param deviceId
     * @param playback
     */
    protected void playback(String media, String deviceId, String playback) {
        fsListen.playfile(media, deviceId, playback);
    }

    /**
     * @param media
     * @param callId
     * @param deviceId
     */
    /**
     * 按键导航收键，收键参数取自 cc_vdn_config 的 dtmf_min/max/retry/dtfm_end
     *
     * @param media
     * @param vdnSchedulePo
     * @param deviceId
     */
    protected void receiveDtmf(String media, VdnSchedulePo vdnSchedulePo, String deviceId) {
        SendMsg play = new SendMsg(deviceId);
        play.addCallCommand("execute");
        play.addExecuteAppName("set");
        play.addExecuteAppArg("playback_delimiter=!");
        fsListen.sendMessage(media, play);

        int min = vdnSchedulePo.getDtmfMin() == null ? 1 : vdnSchedulePo.getDtmfMin();
        int max = vdnSchedulePo.getDtmfMax() == null ? 1 : vdnSchedulePo.getDtmfMax();
        int retry = vdnSchedulePo.getRetry() == null ? 1 : vdnSchedulePo.getRetry();
        String end = StringUtils.isBlank(vdnSchedulePo.getDtmfEnd()) ? "#" : vdnSchedulePo.getDtmfEnd();
        Playback playback = cacheService.getPlayback(Long.parseLong(vdnSchedulePo.getRouteValue()));
        String prompt = playback != null && StringUtils.isNotBlank(playback.getPlayback()) ? playback.getPlayback() : "silence_stream://250";

        SendMsg digits = new SendMsg(deviceId);
        digits.addCallCommand("execute");
        digits.addExecuteAppName("play_and_get_digits");
        digits.addExecuteAppArg(String.format("%d %d %d 5000 %s %s silence_stream://250 SYMWRD_DTMF_RETURN [\\*0-9#]+ 3000", min, max, retry, end, prompt));
        fsListen.sendMessage(media, digits);
    }

    /**
     * 随机生成deviceId
     *
     * @return
     */
    protected String getDeviceId() {
        return RandomStringUtils.randomNumeric(16);
    }


    /**
     * @param callInfo
     * @param deviceInfo
     */
    protected void doNextCommand(CallInfo callInfo, DeviceInfo deviceInfo, NextCommand nextCommand) {
        if (nextCommand == null) {
            return;
        }
        callInfo.getNextCommands().remove(nextCommand);
        switch (nextCommand.getNextType()) {
            case NEXT_QUEUE_PLAY:
                fsListen.playfile(callInfo.getMediaHost(), deviceInfo.getDeviceId(), "queue.wav");
                break;

            case NEXT_QUEUE_OVERFLOW_GROUP:
                GroupInfo groupInfo = cacheService.getGroupInfo(Long.parseLong(nextCommand.getNextValue()));
                groupHandler.hander(callInfo, groupInfo, deviceInfo.getDeviceId());
                break;

            case NEXT_QUEUE_OVERFLOW_IVR:
                break;

            case NEXT_QUEUE_OVERFLOW_VDN:
                break;

            case NEXT_HANGUP:
                fsListen.hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), deviceInfo.getDeviceId());
                break;

            case NEXT_VDN:
                break;

            case NEXT_GROUP:
                break;

            case NEXT_IVR:
                break;

            default:
                break;
        }
        callInfo.getDeviceInfoMap().put(deviceInfo.getDeviceId(), deviceInfo);
        cacheService.addCallInfo(callInfo);
    }


    protected String hiddenNumber(String number) {
        if (StringUtils.isBlank(number)) {
            return null;
        }
        if (number.length() >= 11) {
            return number.substring(0, 3) + "****" + number.substring(7);
        }
        if (number.length() < 3) {
            return number;
        }
        return number.substring(0, 3) + randomHidden(number.length());
    }

    private String randomHidden(int num) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= num; i++) {
            builder.append("*");
        }
        return builder.toString();
    }
}
