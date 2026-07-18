package org.voice9.cc.websocket.handler;

import com.voice9.core.entity.Agent;
import com.voice9.core.entity.RouteGetway;
import com.voice9.core.enums.ErrorCode;
import com.voice9.core.enums.NextType;
import com.voice9.core.po.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.voice9.cc.configration.HandlerType;
import org.voice9.cc.websocket.event.WsTransferEvent;
import org.voice9.cc.websocket.handler.base.WsBaseHandler;
import org.voice9.cc.websocket.response.WsResponseEntity;

import java.time.Instant;

/**
 * Created by dongjb on 2025/09/03
 * <p>
 * 坐席发起转接
 */
@Component
@HandlerType("WS_TRANSFER")
public class WsTransferHandler extends WsBaseHandler<WsTransferEvent> {

    @Override
    public void handleEvent(WsTransferEvent event) {
        logger.info("agent:{} transfer to target:{}", event.getAgentKey(), event.getTarget());

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

        String target = event.getTarget();
        if (StringUtils.isBlank(target)) {
            return;
        }

        // 查找目标坐席
        AgentInfo targetAgent = cacheService.getAgentInfo(target);
        if (targetAgent == null) {
            // 尝试通过SIP号查找
            Agent agent = agentService.getAgentBySip(target);
            if (agent != null) {
                targetAgent = cacheService.getAgentInfo(agent.getAgentKey());
            }
        }

        if (targetAgent != null) {
            // 转坐席
            logger.info("callId:{} transfer to agent:{}", callInfo.getCallId(), targetAgent.getAgentKey());
            String deviceId = getDeviceId();
            RouteGetway routeGetway = cacheService.getRouteGetway(callInfo.getCompanyId(), targetAgent.getCalled());
            if (routeGetway == null) {
                logger.warn("callId:{} transfer route error, target:{}", callInfo.getCallId(), targetAgent.getAgentKey());
                sendMessage(event, new WsResponseEntity<>(ErrorCode.CALL_ROUTE_ERROR, event.getCmd(), event.getAgentKey()));
                return;
            }
            // 防止挂机处理器触发"挂断所有设备"的连锁反应
            callInfo.setEndTime(Instant.now().toEpochMilli());
            // 挂断当前通话的另一侧
            for (String s : callInfo.getDeviceList()) {
                if (!s.equals(agentInfo.getDeviceId())) {
                    hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), s);
                }
            }
            // 创建转接设备
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setCaller(agentInfo.getAgentId());
            deviceInfo.setCalled(targetAgent.getCalled());
            deviceInfo.setCallTime(Instant.now().toEpochMilli());
            deviceInfo.setCallId(callInfo.getCallId());
            deviceInfo.setDeviceId(deviceId);
            deviceInfo.setDeviceType(1);
            deviceInfo.setCdrType(4);
            deviceInfo.setAgentKey(targetAgent.getAgentKey());
            callInfo.getDeviceList().add(deviceId);
            callInfo.getDeviceInfoMap().put(deviceId, deviceInfo);
            callInfo.setAgentKey(targetAgent.getAgentKey());
            callInfo.setCalled(targetAgent.getCalled());
            callInfo.getNextCommands().add(new NextCommand(agentInfo.getDeviceId(), NextType.NEXT_TRANSFER_CALL, deviceId));
            cacheService.addCallInfo(callInfo);
            cacheService.addDevice(deviceId, callInfo.getCallId());
            targetAgent.setCallId(callInfo.getCallId());
            targetAgent.setDeviceId(deviceId);
            fsListen.makeCall(routeGetway, callInfo.getCaller(), targetAgent.getCalled(), callInfo.getCallId(), deviceId, null, null);
            sendMessage(event, new WsResponseEntity<>("TRANSFERRING", event.getAgentKey()));
        } else {
            // 转外线
            logger.info("callId:{} transfer to external:{}", callInfo.getCallId(), target);
            String deviceId = getDeviceId();
            RouteGetway routeGetway = cacheService.getRouteGetway(callInfo.getCompanyId(), target);
            if (routeGetway == null) {
                logger.warn("callId:{} transfer route error, target:{}", callInfo.getCallId(), target);
                sendMessage(event, new WsResponseEntity<>(ErrorCode.CALL_ROUTE_ERROR, event.getCmd(), event.getAgentKey()));
                return;
            }
            // 防止挂机处理器触发"挂断所有设备"的连锁反应
            callInfo.setEndTime(Instant.now().toEpochMilli());
            for (String s : callInfo.getDeviceList()) {
                if (!s.equals(agentInfo.getDeviceId())) {
                    hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), s);
                }
            }
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setCaller(agentInfo.getAgentId());
            deviceInfo.setCalled(target);
            deviceInfo.setCallTime(Instant.now().toEpochMilli());
            deviceInfo.setCallId(callInfo.getCallId());
            deviceInfo.setDeviceId(deviceId);
            deviceInfo.setDeviceType(3);
            deviceInfo.setCdrType(2);
            deviceInfo.setAgentKey(agentInfo.getAgentKey());
            callInfo.getDeviceList().add(deviceId);
            callInfo.getDeviceInfoMap().put(deviceId, deviceInfo);
            callInfo.setCalled(target);
            callInfo.getNextCommands().add(new NextCommand(agentInfo.getDeviceId(), NextType.NEXT_TRANSFER_CALL, deviceId));
            cacheService.addCallInfo(callInfo);
            cacheService.addDevice(deviceId, callInfo.getCallId());
            fsListen.makeCall(routeGetway, callInfo.getCaller(), target, callInfo.getCallId(), deviceId, null, null);
            sendMessage(event, new WsResponseEntity<>("TRANSFERRING", event.getAgentKey()));
        }
    }
}
