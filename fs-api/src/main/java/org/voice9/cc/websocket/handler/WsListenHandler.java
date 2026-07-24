package org.voice9.cc.websocket.handler;

import com.voice9.core.entity.Agent;
import com.voice9.core.entity.RouteGetway;
import com.voice9.core.enums.ErrorCode;
import com.voice9.core.enums.NextType;
import com.voice9.core.po.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.voice9.cc.configration.HandlerType;
import org.voice9.cc.websocket.event.WsMonitorEvent;
import org.voice9.cc.websocket.handler.base.WsBaseHandler;
import org.voice9.cc.websocket.response.WsResponseEntity;

import java.time.Instant;

/**
 * 班长监听（静默监听坐席通话，坐席和客户均无感知）
 */
@Component
@HandlerType("WS_LISTEN")
public class WsListenHandler extends WsBaseHandler<WsMonitorEvent> {

    @Override
    public void handleEvent(WsMonitorEvent event) {
        logger.info("agent:{} listen to target:{}", event.getAgentKey(), event.getTarget());

        String target = event.getTarget();
        if (StringUtils.isBlank(target)) {
            return;
        }

        // 查找目标坐席
        AgentInfo targetAgent = cacheService.getAgentInfo(target);
        if (targetAgent == null) {
            Agent agent = agentService.getAgentBySip(target);
            if (agent != null) {
                targetAgent = cacheService.getAgentInfo(agent.getAgentKey());
            }
        }

        if (targetAgent == null) {
            logger.warn("listen failed, target:{} not found", target);
            sendMessage(event, new WsResponseEntity<>(ErrorCode.TARGET_NOT_FOUND, event.getCmd(), event.getAgentKey()));
            return;
        }
        // 三级兜底：callId → agentKey 反向查 → deviceId 查
        Long targetCallId = targetAgent.getCallId();
        CallInfo callInfo = targetCallId != null ? cacheService.getCallInfo(targetCallId) : null;
        if (callInfo == null) {
            callInfo = cacheService.getCallInfoByAgentKey(targetAgent.getAgentKey());
        }
        if (callInfo == null && StringUtils.isNotBlank(targetAgent.getDeviceId())) {
            callInfo = cacheService.getCallInfo(targetAgent.getDeviceId());
        }
        if (callInfo == null) {
            logger.warn("listen failed, target:{} not in call", target);
            sendMessage(event, new WsResponseEntity<>(ErrorCode.TARGET_NOT_IN_CALL, event.getCmd(), event.getAgentKey()));
            return;
        }

        // 获取班长（发起者）的坐席信息
        AgentInfo supervisor = getAgent(event);

        // 从 callInfo 取目标坐席的 deviceId（避免 Redis agentInfo 中 deviceId 可能为 null）
        String targetDeviceId = null;
        for (String deviceId : callInfo.getDeviceList()) {
            DeviceInfo di = callInfo.getDeviceInfoMap().get(deviceId);
            if (di != null && targetAgent.getAgentKey().equals(di.getAgentKey())) {
                targetDeviceId = deviceId;
                break;
            }
        }
        if (StringUtils.isBlank(targetDeviceId)) {
            logger.warn("listen failed, target:{} device not found in callInfo", target);
            sendMessage(event, new WsResponseEntity<>(ErrorCode.TARGET_NOT_IN_CALL, event.getCmd(), event.getAgentKey()));
            return;
        }

        String supervisorDeviceId = getDeviceId();
        RouteGetway routeGetway = cacheService.getRouteGetway(callInfo.getCompanyId(), supervisor.getCalled());
        if (routeGetway == null) {
            logger.warn("listen route error, supervisor:{}", event.getAgentKey());
            sendMessage(event, new WsResponseEntity<>(ErrorCode.CALL_ROUTE_ERROR, event.getCmd(), event.getAgentKey()));
            return;
        }

        // 创建设备（cdrType=6 监听）
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setCaller(supervisor.getAgentId());
        deviceInfo.setCalled(supervisor.getCalled());
        deviceInfo.setCallTime(Instant.now().toEpochMilli());
        deviceInfo.setCallId(callInfo.getCallId());
        deviceInfo.setDeviceId(supervisorDeviceId);
        deviceInfo.setDeviceType(1);
        deviceInfo.setCdrType(6);
        deviceInfo.setAgentKey(supervisor.getAgentKey());
        callInfo.getDeviceList().add(supervisorDeviceId);
        callInfo.getDeviceInfoMap().put(supervisorDeviceId, deviceInfo);

        callInfo.getNextCommands().add(new NextCommand(supervisorDeviceId, NextType.NEXT_LISTEN_CALL, targetDeviceId));
        cacheService.addCallInfo(callInfo);
        cacheService.addDevice(supervisorDeviceId, callInfo.getCallId());
        supervisor.setCallId(callInfo.getCallId());
        supervisor.setDeviceId(supervisorDeviceId);
        fsListen.makeCall(routeGetway, supervisor.getAgentId(), supervisor.getCalled(), callInfo.getCallId(), supervisorDeviceId, null, 3600);
        sendMessage(event, new WsResponseEntity<>("LISTENING", event.getAgentKey()));
    }
}
