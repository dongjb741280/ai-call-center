package com.voxai.ivr.fs;

import com.voxai.core.entity.Station;
import com.voxai.core.esl.inbound.Client;
import com.voxai.core.esl.inbound.IEslEventListener;
import com.voxai.core.esl.internal.Context;
import com.voxai.core.esl.internal.IModEslApi;
import com.voxai.core.esl.transport.SendMsg;
import com.voxai.core.esl.transport.event.EslEvent;
import com.voxai.core.mapper.StationMapper;
import com.voxai.ivr.flow.IvrExecutor;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IVR 引擎与 FreeSwitch 的 ESL 连接，按 mediaHost(host:port) 管理连接并下发 uuid 命令。
 */
@Component
public class IvrFsClient {
    private static final Logger logger = LoggerFactory.getLogger(IvrFsClient.class);

    private final Map<String, Client> fsClient = new ConcurrentHashMap<>();

    @Autowired
    private StationMapper stationMapper;

    @Autowired
    private IvrExecutor ivrExecutor;

    @PostConstruct
    public void start() {
        Map<String, Object> params = new HashMap<>();
        params.put("applicationType", 4);
        List<Station> stations = stationMapper.selectListByMap(params);
        for (Station station : stations) {
            if (station.getStatus() != null && station.getStatus() == 1) {
                connect(station.getApplicationHost(), station.getApplicationPort(), station.getPwd());
            }
        }
    }

    private void connect(String host, Integer port, String password) {
        Client client = new Client();
        try {
            client.connect(new InetSocketAddress(host, port), password, 3);
            client.setEventSubscriptions(IModEslApi.EventFormat.PLAIN, "all");
            client.addEventListener(new IEslEventListener() {
                @Override
                public void onEslEvent(Context ctx, EslEvent event) {
                    ivrExecutor.onEvent(host + ":" + port, event);
                }

                @Override
                public void onClose(Channel channel) {
                    logger.warn("freeswitch {}:{} channel close", host, port);
                }
            });
            fsClient.put(host + ":" + port, client);
            logger.info("connect freeswitch {}:{} success", host, port);
        } catch (Throwable e) {
            logger.error("connect freeswitch {}:{} error: {}", host, port, e.getMessage(), e);
        }
    }

    public void playback(String mediaHost, String deviceId, String file) {
        SendMsg msg = new SendMsg(deviceId);
        msg.addCallCommand("execute");
        msg.addExecuteAppName("playback");
        msg.addExecuteAppArg(file);
        msg.addAsync();
        sendMessage(mediaHost, msg);
    }

    public void playAndGetDigits(String mediaHost, String deviceId, String file, int maxDigits, int timeoutSeconds) {
        SendMsg set = new SendMsg(deviceId);
        set.addCallCommand("execute");
        set.addExecuteAppName("set");
        set.addExecuteAppArg("playback_delimiter=!");
        sendMessage(mediaHost, set);

        String arg = "1 " + maxDigits + " 1 " + timeoutSeconds + " # " + file
                + " silence_stream://250 SYMWRD_DTMF_RETURN [\\*0-9#]+ 3000";
        SendMsg digits = new SendMsg(deviceId);
        digits.addCallCommand("execute");
        digits.addExecuteAppName("play_and_get_digits");
        digits.addExecuteAppArg(arg);
        sendMessage(mediaHost, digits);
    }

    public void hangup(String mediaHost, String deviceId) {
        SendMsg msg = new SendMsg(deviceId);
        msg.addCallCommand("execute");
        msg.addExecuteAppName("hangup");
        msg.addExecuteAppArg("NORMAL_CLEARING");
        sendMessage(mediaHost, msg);
    }

    private void sendMessage(String mediaHost, SendMsg sendMsg) {
        Client client = fsClient.get(mediaHost);
        if (client == null || !client.isActivate()) {
            logger.warn("media {} not available, drop sendmsg", mediaHost);
            return;
        }
        client.sendMessage(sendMsg);
    }
}
