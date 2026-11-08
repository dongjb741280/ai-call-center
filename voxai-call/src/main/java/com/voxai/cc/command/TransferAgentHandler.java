package com.voxai.cc.command;

import org.apache.commons.lang3.StringUtils;
import com.voxai.core.entity.RouteGetway;
import com.voxai.core.enums.NextType;
import com.voxai.core.po.AgentInfo;
import com.voxai.core.po.CallInfo;
import com.voxai.core.po.DeviceInfo;
import com.voxai.core.po.NextCommand;
import org.springframework.stereotype.Component;
import com.voxai.cc.command.base.BaseHandler;

import java.time.Instant;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
public class TransferAgentHandler extends BaseHandler {

    /**
     * 转接座席
     *
     * @param callInfo
     * @param agentInfo
     * @param thisDeviceId
     */
    public void hanlder(CallInfo callInfo, AgentInfo agentInfo, String thisDeviceId) {
        String deviceId = getDeviceId();
        String caller = agentInfo.getCalled();
        //坐席没有sip或者分机不存在
        if (StringUtils.isBlank(caller)) {
            logger.error("agent:{} sip is error, callId:{}", agentInfo.getAgentKey(), callInfo.getCallId());
            if (callInfo.getTaskId()!=null) { fsListen.hangupCall(callInfo.getMediaHost(),callInfo.getCallId(),thisDeviceId); }
            return;
        }
        RouteGetway routeGetway = cacheService.getRouteGetway(callInfo.getCompanyId(), caller);
        if (routeGetway == null) {
            fsListen.hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), thisDeviceId);
            return;
        }
        if (callInfo.getHiddenCustomer() == 1) {

        }

        logger.info("callId:{} find agent:{}", callInfo.getCallId(), agentInfo.getAgentKey());
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setCaller(agentInfo.getAgentId());
        //deviceInfo.setDisplay(callInfo.getHiddenCustomer() == 1 ? hiddenNumber(callInfo.getCaller()) : callInfo.getCaller());
        deviceInfo.setDisplay(callInfo.getCaller());
        deviceInfo.setCalled(caller);
        deviceInfo.setCallTime(Instant.now().toEpochMilli());
        deviceInfo.setCallId(callInfo.getCallId());
        deviceInfo.setDeviceId(deviceId);
        deviceInfo.setDeviceType(1);
        deviceInfo.setAgentKey(agentInfo.getAgentKey());
        callInfo.getDeviceList().add(deviceId);
        callInfo.getDeviceInfoMap().put(deviceId, deviceInfo);
        callInfo.setAgentKey(agentInfo.getAgentKey());
        if (callInfo.getTaskId() == null) { callInfo.setCalled(agentInfo.getCalled()); }
        else {
            callInfo.setAgentName(agentInfo.getAgentName()); callInfo.setLoginType(agentInfo.getLoginType());
            deviceInfo.setCdrType(2); deviceInfo.setAgentName(agentInfo.getAgentName());
        }
        callInfo.getNextCommands().add(new NextCommand(deviceId, NextType.NEXT_CALL_BRIDGE, thisDeviceId));
        cacheService.addCallInfo(callInfo);
        cacheService.addDevice(deviceId, callInfo.getCallId());

        agentInfo.setCallId(callInfo.getCallId());
        agentInfo.setDeviceId(deviceId);
        if (callInfo.getTaskId() != null) {
            agentInfo.setBeforeState(agentInfo.getAgentState()); agentInfo.setBeforeTime(agentInfo.getStateTime());
            agentInfo.setStateTime(Instant.now().getEpochSecond()); agentInfo.setAgentState(com.voxai.core.po.AgentState.OUT_CALL);
            if (agentInfo.getGroupIds() != null) { groupHandler.agentNotReady(agentInfo); }
        }
        cacheService.addAgentInfo(agentInfo);
        fsListen.makeCall(callInfo.getMediaHost(), routeGetway, callInfo.getCaller(), caller, callInfo.getCallId(), deviceId, null, null);
    }
}
