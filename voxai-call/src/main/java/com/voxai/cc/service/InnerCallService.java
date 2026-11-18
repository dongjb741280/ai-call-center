package com.voxai.cc.service;

import com.alibaba.fastjson.JSON;
import com.voxai.cc.cache.CacheService;
import com.voxai.cc.entity.MakeCallVo;
import com.voxai.cc.exception.BusinessException;
import com.voxai.cc.fs.FsListen;
import com.voxai.cc.fs.event.FsParkEvent;
import com.voxai.cc.websocket.WebSocketHandler;
import com.voxai.cc.websocket.response.WsCallEntity;
import com.voxai.cc.websocket.response.WsResponseEntity;
import com.voxai.core.entity.Agent;
import com.voxai.core.entity.RouteGetway;
import com.voxai.core.enums.*;
import com.voxai.core.po.*;
import com.voxai.core.util.SnowflakeIdWorker;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.voxai.core.esl.transport.SendMsg;
import com.voxai.core.constant.FsConstant;
import java.time.Instant;
import java.util.Objects;

/** The REST, WebSocket and authenticated SIP entries share one inner-call lifecycle. */
@Service
public class InnerCallService {
    @Autowired private CacheService cacheService;
    @Autowired private AgentService agentService;
    @Autowired private FsListen fsListen;
    @Autowired private WebSocketHandler webSocketHandler;
    @Autowired private SnowflakeIdWorker snowflakeIdWorker;

    public void makeCall(MakeCallVo request, AgentInfo caller) {
        AgentInfo current = cacheService.getAgentInfo(caller.getAgentKey());
        start(request, current == null ? caller : current, null);
    }

    public void makeSipCall(FsParkEvent event, AgentInfo caller) {
        MakeCallVo request = new MakeCallVo();
        request.setCalled(event.getCalled());
        request.setCallType(CallType.INNER_CALL);
        start(request, caller, event);
    }

    private AgentInfo target(String called, Long companyId) {
        AgentInfo target = cacheService.getAgentInfo(called);
        if (target == null && called != null && called.matches("[0-9]+")) {
            Agent agent = agentService.getAgentBySip(called);
            if (agent != null && Objects.equals(companyId, agent.getCompanyId())) {
                target = cacheService.getAgentInfo(agent.getAgentKey());
            }
        }
        boolean needsSipPresence = target != null && (target.getAgentState() == null
                || target.getAgentState() == AgentState.LOGOUT
                || (target.getLogoutTime() != null && target.getLogoutTime() > 0));
        boolean sipOnline = needsSipPresence && fsListen.isSipRegistered(target.getCalled());
        if (target != null && sipOnline && target.getCallId() != null
                && cacheService.getCallInfo(target.getCallId()) == null) {
            // A previous service restart can leave ownership fields behind after CDR cleanup.
            target.setCallId(null);
            target.setDeviceId(null);
        }
        if (target == null || !Objects.equals(companyId, target.getCompanyId())
                || ((target.getLogoutTime() != null && target.getLogoutTime() > 0) && !sipOnline)) {
            throw new BusinessException(ErrorCode.AGENT_NOT_ONLINE);
        }
        if (sipOnline && (target.getLogoutTime() == null || target.getLogoutTime() > 0
                || target.getAgentState() == null || target.getAgentState() == AgentState.LOGOUT)) {
            // Make the SIP registration visible to the common state machine as READY.
            target.setLogoutTime(0L);
            target.setBeforeState(AgentState.LOGOUT);
            target.setBeforeTime(target.getStateTime());
            target.setStateTime(Instant.now().getEpochSecond());
            target.setAgentState(AgentState.READY);
            cacheService.addAgentInfo(target);
        }
        return target;
    }

    private void available(AgentInfo agent, ErrorCode error, boolean sipCaller) {
        if (agent.getCallId() != null || isCallState(agent.getAgentState())) {
            throw new BusinessException(error);
        }
        if (!sipCaller && (agent.getAgentState() == null || agent.getAgentState() == AgentState.LOGOUT
                || (agent.getLogoutTime() != null && agent.getLogoutTime() > 0))) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_LOGIN);
        }
        if (!sipCaller && agent.getAgentState() != AgentState.READY && agent.getAgentState() != AgentState.NOT_READY) {
            throw new BusinessException(ErrorCode.AGENT_BUSY);
        }
        if (sipCaller && agent.getAgentState() != null && agent.getAgentState() != AgentState.LOGOUT
                && agent.getAgentState() != AgentState.READY && agent.getAgentState() != AgentState.NOT_READY) {
            throw new BusinessException(ErrorCode.AGENT_BUSY);
        }
        if (StringUtils.isBlank(agent.getCalled())) throw new BusinessException(ErrorCode.CALL_NUMBER_ERROR);
    }

    private boolean isCallState(AgentState state) {
        return state != null && (state.name().contains("CALL") || state == AgentState.TALKING
                || state == AgentState.CONSULT || state == AgentState.CONSULT_TALKING
                || state == AgentState.CONSULTED_TALKING || state == AgentState.CONFERENCE_TALKING
                || state == AgentState.TRANSFER || state == AgentState.HOLD);
    }

    private void start(MakeCallVo request, AgentInfo caller, FsParkEvent sip) {
        AgentInfo callee = target(request.getCalled(), caller.getCompanyId());
        if (Objects.equals(caller.getAgentKey(), callee.getAgentKey())) throw new BusinessException(ErrorCode.AGENT_BUSY);
        available(caller, ErrorCode.AGENT_CALLING, sip != null);
        available(callee, ErrorCode.AGENT_BUSY, false);
        GroupInfo group = cacheService.getGroupInfo(caller.getGroupId());
        if (group == null || !Integer.valueOf(1).equals(group.getStatus())) throw new BusinessException(ErrorCode.AGENT_GROUP_NULL);
        RouteGetway origin = sip == null ? cacheService.getRouteGetway(caller.getCompanyId(), caller.getCalled()) : null;
        if ((sip == null && origin == null) || cacheService.getRouteGetway(caller.getCompanyId(), callee.getCalled()) == null) {
            throw new BusinessException(ErrorCode.CALL_ROUTE_ERROR);
        }
        Long callId = snowflakeIdWorker.nextId();
        String deviceId = sip == null ? RandomStringUtils.randomNumeric(16) : sip.getDeviceId();
        String callerNumber = sip == null ? caller.getCalled() : sip.getAuthenticatedUser();
        CallInfo call = CallInfo.CallInfoBuilder.builder().withCallId(callId).withCallType(CallType.INNER_CALL)
                .withDirection(Direction.OUTBOUND).withCompanyId(caller.getCompanyId()).withGroupId(caller.getGroupId())
                .withAgentKey(caller.getAgentKey()).withAgentName(caller.getAgentName()).withCaller(callerNumber)
                .withCalled(callee.getCalled()).withCallerDisplay(callerNumber).withCalledDisplay(callerNumber)
                .withCallTime(System.currentTimeMillis()).withCtiHost(caller.getHost()).withFollowData(request.getFollowData())
                .withUuid1(request.getUuid1()).withUuid2(request.getUuid2()).build();
        call.setCalledAgentKey(callee.getAgentKey());
        call.getAgentStatesBeforeCall().put(caller.getAgentKey(), caller.getAgentState() == null ? AgentState.LOGOUT : caller.getAgentState());
        call.getAgentStatesBeforeCall().put(callee.getAgentKey(), callee.getAgentState());
        if (sip != null) call.setMediaHost(sip.getRemoteAddress());
        CompanyInfo company = cacheService.getCompany(caller.getCompanyId());
        call.setHiddenCustomer(0); // Internal identities are not customer numbers.
        if (company != null) call.setCdrNotifyUrl(company.getNotifyUrl());
        DeviceInfo device = DeviceInfo.DeviceInfoBuilder.builder().withCallId(callId).withDeviceId(deviceId)
                .withCaller(callerNumber).withCalled(callerNumber).withDisplay(callerNumber).withAgentKey(caller.getAgentKey())
                .withCallTime(call.getCallTime()).withDeviceType(1).withCdrType(3).build();
        device.setAgentName(caller.getAgentName());
        call.getDeviceList().add(deviceId); call.getDeviceInfoMap().put(deviceId, device);
        call.getNextCommands().add(new NextCommand(deviceId, NextType.NEXT_CALL_OTHER, null));
        // Reserve both agents before origination so a second inner call cannot claim either participant.
        if (!cacheService.tryClaimAgentCall(caller.getAgentKey(), callId)) throw new BusinessException(ErrorCode.AGENT_CALLING);
        if (!cacheService.tryClaimAgentCall(callee.getAgentKey(), callId)) {
            cacheService.releaseAgentCall(caller.getAgentKey(), callId);
            throw new BusinessException(ErrorCode.AGENT_BUSY);
        }
        try {
            caller.setCallId(callId); caller.setDeviceId(deviceId);
            callee.setCallId(callId); callee.setDeviceId(null);
            change(caller, AgentState.INNER_CALL); change(callee, AgentState.INNER_CALL);
            cacheService.addCallInfo(call); cacheService.addDevice(deviceId, callId);
            notify(caller, call, AgentState.INNER_CALL);
            if (sip == null) {
                fsListen.makeCall(origin, callerNumber, callerNumber, callId, deviceId, null, null);
            } else {
                // Tag the adopted channel too, allowing event indexing after a voxai-call restart.
                fsListen.sendBgapiMessage(call.getMediaHost(), "uuid_setvar", deviceId + " callId " + callId);
                SendMsg answer = new SendMsg(deviceId);
                answer.addCallCommand(FsConstant.EXECUTE); answer.addExecuteAppName(FsConstant.ANSWER);
                fsListen.sendMessage(call.getMediaHost(), answer);
            }
        } catch (RuntimeException failure) {
            restore(caller, call); restore(callee, call);
            cacheService.removeCallInfo(callId);
            throw failure;
        }
    }

    private void change(AgentInfo agent, AgentState state) {
        agent.setBeforeState(agent.getAgentState() == null ? AgentState.LOGOUT : agent.getAgentState());
        agent.setBeforeTime(agent.getStateTime()); agent.setStateTime(Instant.now().getEpochSecond());
        agent.setAgentState(state); cacheService.addAgentInfo(agent);
        agentService.syncAgentStateMessage(agent);
    }

    private void restore(AgentInfo agent, CallInfo call) {
        AgentInfo latest = cacheService.getAgentInfo(agent.getAgentKey());
        if (latest != null && !Objects.equals(latest.getCallId(), call.getCallId())) {
            cacheService.releaseAgentCall(agent.getAgentKey(), call.getCallId()); return;
        }
        agent.setCallId(null); agent.setDeviceId(null);
        change(agent, call.getAgentStatesBeforeCall().get(agent.getAgentKey()));
        cacheService.releaseAgentCall(agent.getAgentKey(), call.getCallId());
    }

    private void notify(AgentInfo agent, CallInfo call, AgentState state) {
        if (StringUtils.isBlank(agent.getHost())) return;
        WsCallEntity data = new WsCallEntity(); data.setCallId(call.getCallId()); data.setCallType(call.getCallType());
        data.setCaller(call.getCaller()); data.setCalled(call.getCalled()); data.setDirection(call.getDirection()); data.setAgentState(state);
        webSocketHandler.sendMessgae(agent, JSON.toJSONString(new WsResponseEntity<>(state.name(), agent.getAgentKey(), data)));
    }
}
