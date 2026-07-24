package org.voice9.cc.websocket.handler;

import com.alibaba.fastjson.JSON;
import com.voice9.core.po.*;
import org.apache.commons.lang3.StringUtils;
import com.voice9.core.enums.ErrorCode;
import org.springframework.stereotype.Component;
import org.voice9.cc.configration.HandlerType;
import org.voice9.cc.websocket.event.base.WsBaseEvent;
import org.voice9.cc.websocket.handler.base.WsBaseHandler;
import org.voice9.cc.websocket.response.WsConferenceResponse;
import org.voice9.cc.websocket.response.WsResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by dongjb on 2025/09/03
 * <p>
 * 咨询转三方会议（将客户、坐席、被咨询方合并为三方通话）
 */
@Component
@HandlerType("WS_CONSULT_PARTY")
public class WsConsultPartyHandler extends WsBaseHandler<WsBaseEvent> {

    @Override
    public void handleEvent(WsBaseEvent event) {
        logger.info("agent:{} consult party", event.getAgentKey());

        AgentInfo agentInfo = getAgent(event);
        if (agentInfo.getCallId() == null || StringUtils.isBlank(agentInfo.getDeviceId())) {
            logger.warn("agent:{} is not in any call", event.getAgentKey());
            sendMessage(event, new WsResponseEntity<>(ErrorCode.CALL_NOT_EXIST, event.getCmd(), event.getAgentKey()));
            return;
        }

        CallInfo callInfo = cacheService.getCallInfo(agentInfo.getCallId());
        if (callInfo == null) {
            sendMessage(event, new WsResponseEntity<>(ErrorCode.CALL_NOT_EXIST, event.getCmd(), event.getAgentKey()));
            return;
        }

        // 找到客户设备（HOLD）和被咨询方设备（cdrType=5且非HOLD）
        String customerDeviceId = null;
        String consultedDeviceId = null;
        String consultedAgentKey = null;
        for (String deviceId : callInfo.getDeviceList()) {
            DeviceInfo deviceInfo = callInfo.getDeviceInfoMap().get(deviceId);
            if (deviceInfo == null || deviceId.equals(agentInfo.getDeviceId())) {
                continue;
            }
            if (AgentState.HOLD.name().equals(deviceInfo.getState())) {
                customerDeviceId = deviceId;
            } else if (deviceInfo.getCdrType() == 5) {
                consultedDeviceId = deviceId;
                consultedAgentKey = deviceInfo.getAgentKey();
            }
        }

        if (StringUtils.isBlank(customerDeviceId) || StringUtils.isBlank(consultedDeviceId)) {
            logger.warn("callId:{} consult party failed, customerDevice:{}, consultedDevice:{}",
                    callInfo.getCallId(), customerDeviceId, consultedDeviceId);
            sendMessage(event, new WsResponseEntity<>(ErrorCode.CALL_NOT_EXIST, event.getCmd(), event.getAgentKey()));
            return;
        }

        logger.info("callId:{} consult party, agent:{}, customer:{}, consulted:{}",
                callInfo.getCallId(), agentInfo.getDeviceId(), customerDeviceId, consultedDeviceId);

        // 1. 停止客户侧保持音
        playBreak(callInfo.getMediaHost(), callInfo.getCallId(), customerDeviceId);

        // 2. 断开坐席与被咨询方的桥接，park双方
        bridgeBreak(callInfo.getMediaHost(), agentInfo.getDeviceId());

        // 3. 生成会议号
        String conferenceName = callInfo.getCallId() + "-" + UUID.randomUUID().toString().substring(0, 8);

        // 4. 将三方加入会议
        fsListen.conference(callInfo.getMediaHost(), agentInfo.getDeviceId(), conferenceName);
        fsListen.conference(callInfo.getMediaHost(), customerDeviceId, conferenceName);
        fsListen.conference(callInfo.getMediaHost(), consultedDeviceId, conferenceName);

        // 5. 更新设备状态
        DeviceInfo customerDevice = callInfo.getDeviceInfoMap().get(customerDeviceId);
        DeviceInfo consultedDeviceInfo = callInfo.getDeviceInfoMap().get(consultedDeviceId);
        if (customerDevice != null) {
            customerDevice.setState(null);
            customerDevice.setConference(conferenceName);
        }
        if (consultedDeviceInfo != null) {
            consultedDeviceInfo.setConference(conferenceName);
        }
        DeviceInfo agentDevice = callInfo.getDeviceInfoMap().get(agentInfo.getDeviceId());
        if (agentDevice != null) {
            agentDevice.setConference(conferenceName);
        }

        callInfo.setConference(conferenceName);
        cacheService.addCallInfo(callInfo);

        // 6. 给坐席推送会议状态
        WsConferenceResponse conferenceResponse = new WsConferenceResponse();
        conferenceResponse.setCallId(callInfo.getCallId());
        conferenceResponse.setCallType(callInfo.getCallType());
        conferenceResponse.setAgentState(AgentState.CONFERENCE_TALKING);
        conferenceResponse.setConference(conferenceName);
        List<String> conferenceList = new ArrayList<>();
        conferenceList.add(agentInfo.getAgentKey());
        if (StringUtils.isNotBlank(consultedAgentKey)) {
            conferenceList.add(consultedAgentKey);
        }
        conferenceResponse.setConferenceList(conferenceList);
        sendMessage(event, new WsResponseEntity<WsConferenceResponse>("CONFERENCE", event.getAgentKey(), conferenceResponse));

        // 7. 给被咨询方坐席推送会议状态
        if (StringUtils.isNotBlank(consultedAgentKey)) {
            AgentInfo consultedAgent = cacheService.getAgentInfo(consultedAgentKey);
            if (consultedAgent != null) {
                WsConferenceResponse consultedResponse = new WsConferenceResponse();
                consultedResponse.setCallId(callInfo.getCallId());
                consultedResponse.setCallType(callInfo.getCallType());
                consultedResponse.setAgentState(AgentState.CONFERENCE_TALKING);
                consultedResponse.setConference(conferenceName);
                List<String> list = new ArrayList<>();
                list.add(agentInfo.getAgentKey());
                list.add(consultedAgentKey);
                consultedResponse.setConferenceList(list);
                sendMessage(consultedAgent, JSON.toJSONString(new WsResponseEntity<WsConferenceResponse>("CONFERENCE", consultedAgentKey, consultedResponse)));
            }
        }
    }
}
