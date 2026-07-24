package org.voice9.cc.websocket.handler;

import com.voice9.core.po.*;
import org.apache.commons.lang3.StringUtils;
import com.voice9.core.enums.ErrorCode;
import org.springframework.stereotype.Component;
import org.voice9.cc.configration.HandlerType;
import org.voice9.cc.websocket.event.base.WsBaseEvent;
import org.voice9.cc.websocket.handler.base.WsBaseHandler;
import org.voice9.cc.websocket.response.WsResponseEntity;

/**
 * Created by dongjb on 2025/09/03
 * <p>
 * 坐席取消咨询（挂断被咨询方，恢复与客户的通话）
 */
@Component
@HandlerType("WS_CONSULT_CANCEL")
public class WsConsultCancelHandler extends WsBaseHandler<WsBaseEvent> {

    @Override
    public void handleEvent(WsBaseEvent event) {
        logger.info("agent:{} consult cancel", event.getAgentKey());

        AgentInfo agentInfo = getAgent(event);
        if (agentInfo.getCallId() == null || StringUtils.isBlank(agentInfo.getDeviceId())) {
            sendMessage(event, new WsResponseEntity<>(ErrorCode.CALL_NOT_EXIST, event.getCmd(), event.getAgentKey()));
            return;
        }

        CallInfo callInfo = cacheService.getCallInfo(agentInfo.getCallId());
        if (callInfo == null) {
            sendMessage(event, new WsResponseEntity<>(ErrorCode.CALL_NOT_EXIST, event.getCmd(), event.getAgentKey()));
            return;
        }

        // 找到被咨询方（cdrType=5且非HOLD）和客户（HOLD状态）
        String customerDeviceId = null;
        String consultedDeviceId = null;
        for (String deviceId : callInfo.getDeviceList()) {
            DeviceInfo deviceInfo = callInfo.getDeviceInfoMap().get(deviceId);
            if (deviceInfo == null || deviceId.equals(agentInfo.getDeviceId())) {
                continue;
            }
            if (AgentState.HOLD.name().equals(deviceInfo.getState())) {
                customerDeviceId = deviceId;
            } else if (deviceInfo.getCdrType() == 5) {
                consultedDeviceId = deviceId;
            }
        }

        if (StringUtils.isBlank(customerDeviceId) || StringUtils.isBlank(consultedDeviceId)) {
            logger.warn("callId:{} consult cancel failed, customerDevice:{}, consultedDevice:{}",
                    callInfo.getCallId(), customerDeviceId, consultedDeviceId);
            sendMessage(event, new WsResponseEntity<>(ErrorCode.CALL_NOT_EXIST, event.getCmd(), event.getAgentKey()));
            return;
        }

        logger.info("callId:{} consult cancel, agent:{}, customer:{}, consulted:{}",
                callInfo.getCallId(), agentInfo.getDeviceId(), customerDeviceId, consultedDeviceId);

        // 1. 断开坐席与被咨询方的桥接
        bridgeBreak(callInfo.getMediaHost(), agentInfo.getDeviceId());

        // 2. 挂断被咨询方
        hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), consultedDeviceId);

        // 3. 停止客户侧保持音
        playBreak(callInfo.getMediaHost(), callInfo.getCallId(), customerDeviceId);

        // 4. 恢复坐席与客户的桥接
        bridgeCall(callInfo.getMediaHost(), callInfo.getCallId(), agentInfo.getDeviceId(), customerDeviceId);

        // 5. 清除客户HOLD状态
        DeviceInfo customerDevice = callInfo.getDeviceInfoMap().get(customerDeviceId);
        if (customerDevice != null) {
            customerDevice.setState(null);
        }

        cacheService.addCallInfo(callInfo);
        sendMessage(event, new WsResponseEntity<>("CONSULT_CANCELED", event.getAgentKey()));
    }
}
