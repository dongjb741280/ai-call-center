package org.voice9.cc.websocket.handler;

import com.voice9.core.entity.Agent;
import com.voice9.core.entity.RouteGetway;
import com.voice9.core.enums.ErrorCode;
import com.voice9.core.enums.NextType;
import com.voice9.core.po.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.voice9.cc.configration.HandlerType;
import org.voice9.cc.websocket.event.WsConsultEvent;
import org.voice9.cc.websocket.handler.base.WsBaseHandler;
import org.voice9.cc.websocket.response.WsResponseEntity;

import java.time.Instant;

/**
 * Created by dongjb on 2025/09/03
 * <p>
 * 坐席发起咨询（保持当前客户，私下呼叫目标坐席或外线）
 */
@Component
@HandlerType("WS_CONSULT")
public class WsConsultHandler extends WsBaseHandler<WsConsultEvent> {

    @Override
    public void handleEvent(WsConsultEvent event) {
        logger.info("agent:{} consult to target:{}", event.getAgentKey(), event.getTarget());

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

        // 当前通话至少有2个设备（坐席 + 客户），找到客户侧的deviceId
        String customerDeviceId = getDeviceId(callInfo, agentInfo.getDeviceId());
        if (StringUtils.isBlank(customerDeviceId)) {
            logger.warn("callId:{} no customer device found", callInfo.getCallId());
            sendMessage(event, new WsResponseEntity<>(ErrorCode.CALL_NOT_EXIST, event.getCmd(), event.getAgentKey()));
            return;
        }

        // 查找目标坐席
        AgentInfo targetAgent = cacheService.getAgentInfo(target);
        if (targetAgent != null) {
            // 咨询坐席
            logger.info("callId:{} consult to agent:{}", callInfo.getCallId(), targetAgent.getAgentKey());
            String deviceId = getDeviceId();
            RouteGetway routeGetway = cacheService.getRouteGetway(callInfo.getCompanyId(), targetAgent.getCalled());
            if (routeGetway == null) {
                logger.warn("callId:{} consult route error, target:{}", callInfo.getCallId(), targetAgent.getAgentKey());
                sendMessage(event, new WsResponseEntity<>(ErrorCode.CALL_ROUTE_ERROR, event.getCmd(), event.getAgentKey()));
                return;
            }

            // 创建咨询设备
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setCaller(agentInfo.getAgentId());
            deviceInfo.setCalled(targetAgent.getCalled());
            deviceInfo.setCallTime(Instant.now().toEpochMilli());
            deviceInfo.setCallId(callInfo.getCallId());
            deviceInfo.setDeviceId(deviceId);
            deviceInfo.setDeviceType(1);
            deviceInfo.setCdrType(5);
            deviceInfo.setAgentKey(targetAgent.getAgentKey());
            callInfo.getDeviceList().add(deviceId);
            callInfo.getDeviceInfoMap().put(deviceId, deviceInfo);

            // NextCommand: deviceId=坐席leg, nextValue=客户leg(用于HOLD), 目标应答后走到NEXT_CONSULT_AGENT
            callInfo.getNextCommands().add(new NextCommand(agentInfo.getDeviceId(), NextType.NEXT_CONSULT_AGENT, customerDeviceId));
            cacheService.addCallInfo(callInfo);
            cacheService.addDevice(deviceId, callInfo.getCallId());
            targetAgent.setCallId(callInfo.getCallId());
            targetAgent.setDeviceId(deviceId);
            fsListen.makeCall(routeGetway, callInfo.getCaller(), targetAgent.getCalled(), callInfo.getCallId(), deviceId, null, null);
            sendMessage(event, new WsResponseEntity<>("CONSULTING", event.getAgentKey()));
        } else {
            // 咨询外线
            logger.info("callId:{} consult to external:{}", callInfo.getCallId(), target);
            String deviceId = getDeviceId();
            RouteGetway routeGetway = cacheService.getRouteGetway(callInfo.getCompanyId(), target);
            if (routeGetway == null) {
                logger.warn("callId:{} consult route error, target:{}", callInfo.getCallId(), target);
                sendMessage(event, new WsResponseEntity<>(ErrorCode.CALL_ROUTE_ERROR, event.getCmd(), event.getAgentKey()));
                return;
            }

            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setCaller(agentInfo.getAgentId());
            deviceInfo.setCalled(target);
            deviceInfo.setCallTime(Instant.now().toEpochMilli());
            deviceInfo.setCallId(callInfo.getCallId());
            deviceInfo.setDeviceId(deviceId);
            deviceInfo.setDeviceType(3);
            deviceInfo.setCdrType(5);
            deviceInfo.setAgentKey(agentInfo.getAgentKey());
            callInfo.getDeviceList().add(deviceId);
            callInfo.getDeviceInfoMap().put(deviceId, deviceInfo);

            callInfo.getNextCommands().add(new NextCommand(agentInfo.getDeviceId(), NextType.NEXT_CONSULT_CALLOUT, customerDeviceId));
            cacheService.addCallInfo(callInfo);
            cacheService.addDevice(deviceId, callInfo.getCallId());
            fsListen.makeCall(routeGetway, callInfo.getCaller(), target, callInfo.getCallId(), deviceId, null, null);
            sendMessage(event, new WsResponseEntity<>("CONSULTING", event.getAgentKey()));
        }
    }
}
