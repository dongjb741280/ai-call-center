package com.voxai.cc.service;

import com.voxai.cc.cache.CacheService;
import com.voxai.cc.entity.MakeCallVo;
import com.voxai.cc.exception.BusinessException;
import com.voxai.cc.fs.FsListen;
import com.voxai.cc.fs.event.FsParkEvent;
import com.voxai.cc.websocket.WebSocketHandler;
import com.voxai.core.entity.Agent;
import com.voxai.core.entity.RouteGetway;
import com.voxai.core.enums.*;
import com.voxai.core.po.*;
import com.voxai.core.util.SnowflakeIdWorker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class InnerCallServiceTest {
    InnerCallService service = new InnerCallService();
    CacheService cache = mock(CacheService.class);
    AgentService agents = mock(AgentService.class);
    FsListen fs = mock(FsListen.class);
    AgentInfo caller = agent("agent1001", "1001"), callee = agent("agent1002", "1002");
    MakeCallVo request = new MakeCallVo();
    RouteGetway gateway = new RouteGetway();

    static AgentInfo agent(String key, String sip) {
        AgentInfo agent = new AgentInfo(); agent.setAgentKey(key); agent.setAgentId(sip);
        agent.setCompanyId(1L); agent.setGroupId(1L); agent.setLoginType(2);
        agent.setSips(Collections.singletonList(sip)); agent.setAgentState(AgentState.READY); return agent;
    }
    @BeforeEach void setup() {
        ReflectionTestUtils.setField(service,"cacheService",cache);
        ReflectionTestUtils.setField(service,"agentService",agents);
        ReflectionTestUtils.setField(service,"fsListen",fs);
        ReflectionTestUtils.setField(service,"webSocketHandler",mock(WebSocketHandler.class));
        ReflectionTestUtils.setField(service,"snowflakeIdWorker",new SnowflakeIdWorker(0,0));
        when(cache.getAgentInfo(caller.getAgentKey())).thenReturn(caller);
        when(cache.getAgentInfo(callee.getAgentKey())).thenReturn(callee);
        when(cache.tryClaimAgentCall(anyString(), anyLong())).thenReturn(true);
        when(cache.getRouteGetway(eq(1L), anyString())).thenReturn(gateway);
        GroupInfo group = new GroupInfo(); group.setStatus(1);
        when(cache.getGroupInfo(1L)).thenReturn(group);
        request.setCallType(CallType.INNER_CALL); request.setCalled("agent1002");
    }
    CallInfo call() {
        ArgumentCaptor<CallInfo> result = ArgumentCaptor.forClass(CallInfo.class);
        verify(cache).addCallInfo(result.capture()); return result.getValue();
    }
    @Test void reservesBothAgentsAndOriginatesOnlyCallerUntilAnswer() {
        callee.setAgentState(AgentState.NOT_READY);
        service.makeCall(request,caller);
        CallInfo call = call();
        assertEquals(CallType.INNER_CALL,call.getCallType()); assertEquals("1002",call.getCalled());
        assertEquals("agent1002",call.getCalledAgentKey());
        assertEquals(AgentState.READY,call.getAgentStatesBeforeCall().get("agent1001"));
        assertEquals(AgentState.NOT_READY,call.getAgentStatesBeforeCall().get("agent1002"));
        assertEquals(caller.getCallId(),callee.getCallId()); assertNotNull(callee.getCallId());
        assertEquals(3,call.getDeviceInfoMap().values().iterator().next().getCdrType());
        verify(fs).makeCall(eq(gateway), eq("1001"), eq("1001"), eq(call.getCallId()), anyString(), isNull(), isNull());
    }
    @Test void resolvesSipNumberToSameTenantAgent() {
        request.setCalled("1002"); Agent row = new Agent(); row.setAgentKey("agent1002"); row.setCompanyId(1L);
        when(agents.getAgentBySip("1002")).thenReturn(row);
        service.makeCall(request,caller); assertEquals("agent1002",call().getCalledAgentKey());
    }
    @Test void offlineTargetIsRejectedBeforeReservingOrDialing() {
        callee.setLogoutTime(1L);
        assertError(ErrorCode.AGENT_NOT_ONLINE);
    }

    @Test void loggedOutCallerGetsLoginErrorInsteadOfCallInProgress() {
        caller.setAgentState(AgentState.LOGOUT); caller.setLogoutTime(1L);
        assertError(ErrorCode.ACCOUNT_NOT_LOGIN);
    }

    @Test void callerMustChooseReadyOrNotReadyBeforeStartingAnInnerCall() {
        caller.setAgentState(AgentState.LOGIN);
        assertError(ErrorCode.AGENT_BUSY);
    }

    @Test void registeredZoiperIsOnlineWithoutPlatformWebsocketLogin() {
        callee.setLogoutTime(1L); callee.setAgentState(AgentState.LOGOUT);
        when(fs.isSipRegistered("1002")).thenReturn(true);
        service.makeCall(request, caller);
        assertEquals(AgentState.INNER_CALL, callee.getAgentState());
        assertEquals(0L, callee.getLogoutTime());
        assertNotNull(callee.getCallId());
    }

    @Test void staleSipOwnershipIsClearedOnlyWhenRegistrationConfirmsTheTerminal() {
        callee.setLogoutTime(1L); callee.setAgentState(AgentState.LOGOUT); callee.setCallId(123L);
        when(fs.isSipRegistered("1002")).thenReturn(true);
        when(cache.getCallInfo(123L)).thenReturn(null);
        service.makeCall(request, caller);
        assertEquals(AgentState.INNER_CALL, callee.getAgentState());
    }
    @Test void busyTargetIsRejectedEvenIfItsStateStillSaysReady() {
        callee.setCallId(123L); assertError(ErrorCode.AGENT_BUSY);
    }
    @Test void crossCompanyTargetIsRejected() {
        callee.setCompanyId(2L); assertError(ErrorCode.AGENT_NOT_ONLINE);
    }
    @Test void selfCallIsRejected() { request.setCalled("agent1001"); assertError(ErrorCode.AGENT_BUSY); }
    @Test void missingCalleeRouteDoesNotLeaveReservations() {
        when(cache.getRouteGetway(1L,"1002")).thenReturn(null); assertError(ErrorCode.CALL_ROUTE_ERROR);
    }
    void assertError(ErrorCode expected) {
        BusinessException error = assertThrows(BusinessException.class,()->service.makeCall(request,caller));
        assertEquals(expected.getCode(),error.getErrorCode());
        verify(cache,never()).tryClaimAgentCall(anyString(),anyLong());
        verify(fs,never()).makeCall(any(RouteGetway.class),anyString(),anyString(),anyLong(),anyString(),isNull(),isNull());
    }
    @Test void losingCalleeReservationReleasesCallerWithoutDialing() {
        when(cache.tryClaimAgentCall(eq("agent1002"),anyLong())).thenReturn(false);
        assertThrows(BusinessException.class,()->service.makeCall(request,caller));
        verify(cache).releaseAgentCall(eq("agent1001"),anyLong());
        verify(fs,never()).makeCall(any(RouteGetway.class),anyString(),anyString(),anyLong(),anyString(),isNull(),isNull());
        assertNull(caller.getCallId()); assertNull(callee.getCallId());
    }
    @Test void originateExceptionRestoresBothAgents() {
        doThrow(new IllegalStateException("ESL disconnected")).when(fs).makeCall(any(RouteGetway.class),anyString(),anyString(),anyLong(),anyString(),isNull(),isNull());
        assertThrows(IllegalStateException.class,()->service.makeCall(request,caller));
        assertNull(caller.getCallId()); assertNull(callee.getCallId());
        assertEquals(AgentState.READY,callee.getAgentState());
        verify(cache).removeCallInfo(anyLong());
    }
    @Test void sipEntryAdoptsExistingCallerChannelInsteadOfCallingItAgain() {
        FsParkEvent event = new FsParkEvent(); event.setAuthenticatedUser("1001"); event.setCalled("agent1002");
        event.setDeviceId("sip-channel"); event.setRemoteAddress("fs:8021");
        service.makeSipCall(event,caller);
        assertEquals("sip-channel",call().getDeviceList().get(0));
        verify(fs).sendMessage(eq("fs:8021"),any());
        verify(fs).sendBgapiMessage(eq("fs:8021"),eq("uuid_setvar"),contains("sip-channel callId "));
        verify(fs,never()).makeCall(any(RouteGetway.class),anyString(),anyString(),anyLong(),anyString(),any(),any());
    }
}
