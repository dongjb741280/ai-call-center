package org.voice9.cc.websocket.handler;

import com.voice9.core.po.*;
import org.apache.commons.lang3.StringUtils;
import com.voice9.core.enums.ErrorCode;
import org.springframework.stereotype.Component;
import org.voice9.cc.configration.HandlerType;
import org.voice9.cc.websocket.event.WsConsultEvent;
import org.voice9.cc.websocket.handler.base.WsBaseHandler;
import org.voice9.cc.websocket.response.WsResponseEntity;

import java.time.Instant;

/**
 * Created by dongjb on 2025/09/03
 * <p>
 * 咨询转接（咨询完成后将客户交给被咨询方，坐席退出通话）
 */
@Component
@HandlerType("WS_CONSULT_TRANSFER")
public class WsConsultTransferHandler extends WsBaseHandler<WsConsultEvent> {

    @Override
    public void handleEvent(WsConsultEvent event) {
        logger.info("agent:{} consult transfer, target:{}", event.getAgentKey(), event.getTarget());

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

        // 找到客户设备（HOLD）和被咨询方设备（cdrType=5且非HOLD）
        String customerDeviceId = null;
        String consultedDeviceId = null;
        DeviceInfo consultedDevice = null;
        for (String deviceId : callInfo.getDeviceList()) {
            DeviceInfo deviceInfo = callInfo.getDeviceInfoMap().get(deviceId);
            if (deviceInfo == null || deviceId.equals(agentInfo.getDeviceId())) {
                continue;
            }
            if (AgentState.HOLD.name().equals(deviceInfo.getState())) {
                customerDeviceId = deviceId;
            } else if (deviceInfo.getCdrType() == 5) {
                consultedDeviceId = deviceId;
                consultedDevice = deviceInfo;
            }
        }

        if (StringUtils.isBlank(customerDeviceId) || StringUtils.isBlank(consultedDeviceId)) {
            logger.warn("callId:{} consult transfer failed, customerDevice:{}, consultedDevice:{}",
                    callInfo.getCallId(), customerDeviceId, consultedDeviceId);
            sendMessage(event, new WsResponseEntity<>(ErrorCode.CALL_NOT_EXIST, event.getCmd(), event.getAgentKey()));
            return;
        }

        logger.info("callId:{} consult transfer, customer:{}, consulted:{}",
                callInfo.getCallId(), customerDeviceId, consultedDeviceId);

        // 1. 转移前设置 endTime 防止挂机处理器连锁挂断
        callInfo.setEndTime(Instant.now().toEpochMilli());

        // 2. 断开坐席与被咨询方的桥接
        bridgeBreak(callInfo.getMediaHost(), agentInfo.getDeviceId());

        // 3. 停止客户侧保持音
        playBreak(callInfo.getMediaHost(), callInfo.getCallId(), customerDeviceId);

        // 4. 桥接客户与被咨询方
        bridgeCall(callInfo.getMediaHost(), callInfo.getCallId(), customerDeviceId, consultedDeviceId);

        // 5. 挂断坐席leg
        hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), agentInfo.getDeviceId());

        // 6. 如果被咨询方是坐席，更新 callInfo 的 agentKey
        if (consultedDevice != null && StringUtils.isNotBlank(consultedDevice.getAgentKey())) {
            callInfo.setAgentKey(consultedDevice.getAgentKey());
        }

        // 7. 清除客户HOLD状态
        DeviceInfo customerDevice = callInfo.getDeviceInfoMap().get(customerDeviceId);
        if (customerDevice != null) {
            customerDevice.setState(null);
        }

        cacheService.addCallInfo(callInfo);
        sendMessage(event, new WsResponseEntity<>("CONSULT_TRANSFERRED", event.getAgentKey()));
    }
}
