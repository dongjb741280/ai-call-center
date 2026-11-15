package com.voxai.cc.fs.handler;

import com.alibaba.fastjson.JSON;
import com.voxai.cc.cache.CacheService;
import com.voxai.cc.fs.FsListen;
import com.voxai.cc.fs.event.*;
import com.voxai.cc.service.AgentService;
import com.voxai.cc.service.CallCdrService;
import com.voxai.cc.websocket.WebSocketHandler;
import com.voxai.core.enums.*;
import com.voxai.core.po.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class InnerCallLifecycleTest {
    CacheService cache = mock(CacheService.class);
    FsListen fs = mock(FsListen.class);
    AgentService agents = mock(AgentService.class);
    CallCdrService cdr = mock(CallCdrService.class);
    WebSocketHandler ws = mock(WebSocketHandler.class);
    CallInfo call = new CallInfo();
    AgentInfo a = agent("a"), b = agent("b");
    DeviceInfo da = device("da", "a"), db = device("db", "b");
    GroupInfo group = new GroupInfo();

    @BeforeEach void setup() {
        call.setCallId(900L); call.setCallType(CallType.INNER_CALL);
        call.setDirection(Direction.OUTBOUND); call.setAgentKey("a"); call.setGroupId(1L);
        call.setCompanyId(1L); call.setCallTime(1700000000000L); call.setMediaHost("fs:8021");
        call.getDeviceList().addAll(Arrays.asList("da", "db"));
        call.getDeviceInfoMap().put("da", da); call.getDeviceInfoMap().put("db", db);
        group.setRecordType(2);
        when(cache.getCallInfo(anyString())).thenReturn(call);
        when(cache.getGroupInfo(1L)).thenReturn(group);
        when(cache.getAgentInfo("a")).thenReturn(a); when(cache.getAgentInfo("b")).thenReturn(b);
    }
    static AgentInfo agent(String key) {
        AgentInfo a = new AgentInfo(); a.setAgentKey(key); a.setHost("host");
        a.setCallId(900L); a.setAgentState(AgentState.INNER_CALL); a.setHiddenCustomer(0); return a;
    }
    static DeviceInfo device(String id, String key) {
        DeviceInfo d = new DeviceInfo(); d.setDeviceId(id); d.setCallId(900L);
        d.setAgentKey(key); d.setDeviceType(1); d.setCdrType(3); return d;
    }
    void inject(Object handler) {
        ReflectionTestUtils.setField(handler,"cacheService",cache);
        ReflectionTestUtils.setField(handler,"fsListen",fs);
        ReflectionTestUtils.setField(handler,"agentService",agents);
        ReflectionTestUtils.setField(handler,"callCdrService",cdr);
        ReflectionTestUtils.setField(handler,"webSocketHandler",ws);
        ReflectionTestUtils.setField(handler,"recordPath","/record/");
        ReflectionTestUtils.setField(handler,"recordFile","wav");
    }
    @Test void bridgeUpdatesBothAgentsAndRecordsCallerLegOnceEvenWhenCalleeEventArrivesFirst() {
        FsBridgeHandler handler = new FsBridgeHandler(); inject(handler);
        FsBridgeEvent event = new FsBridgeEvent(); event.setDeviceId("db"); event.setOtherUniqueId("da");
        event.setTimestamp(1700000010000000L); event.setRemoteAddress("fs:8021");
        handler.handleEvent(event); handler.handleEvent(event);
        assertEquals(AgentState.TALKING, a.getAgentState());
        assertEquals(AgentState.TALKING, b.getAgentState());
        verify(cache, atLeastOnce()).addAgentInfo(b);
        assertNotNull(da.getRecord()); assertNull(db.getRecord());
        verify(fs, times(1)).sendBgapiMessage(eq("fs:8021"), eq("uuid_record"), contains("da start "));
        verify(ws, atLeastOnce()).sendMessgae(eq(b), argThat(s -> "TALKING".equals(JSON.parseObject(s).getString("type"))));
    }
    @Test void calleeParkNotifiesCalleeAndReservesBusyState() {
        group.setRecordType(0);
        FsParkHandler handler = new FsParkHandler(); inject(handler);
        FsParkEvent event = new FsParkEvent(); event.setDeviceId("db"); event.setDirection("outbound");
        event.setTimestamp(1700000005000000L);
        handler.handleEvent(event);
        assertEquals(AgentState.IN_CALL_RING,b.getAgentState());
        verify(cache).addAgentInfo(b);
        verify(ws).sendMessgae(eq(b), argThat(s -> "b".equals(JSON.parseObject(s).getString("agentKey"))));
    }

    @Test void authenticatedSipWithEqualViaAndContactPortsEntersPlatformInnerCall() {
        FsParkHandler handler = new FsParkHandler(); inject(handler);
        com.voxai.cc.service.InnerCallService inner = mock(com.voxai.cc.service.InnerCallService.class);
        ReflectionTestUtils.setField(handler,"innerCallService",inner);
        ReflectionTestUtils.setField(handler,"snowflakeIdWorker",new com.voxai.core.util.SnowflakeIdWorker(0,0));
        when(cache.getCallInfo("new-sip")).thenReturn(null);
        com.voxai.core.entity.Agent source = new com.voxai.core.entity.Agent(); source.setAgentKey("a");
        when(agents.getAgentBySip("1001")).thenReturn(source);
        when(agents.getAgentBySip("1002")).thenReturn(new com.voxai.core.entity.Agent());
        a.setGroupId(1L);
        FsParkEvent event = new FsParkEvent(); event.setDeviceId("new-sip"); event.setDirection("inbound");
        event.setAuthenticatedUser("1001"); event.setCaller("spoofed"); event.setCalled("1002");
        event.setSipPort("5060"); event.setSipContactPort("5060");
        handler.handleEvent(event);
        verify(inner).makeSipCall(event,a); assertEquals("1001",event.getCaller());
        verify(cache,never()).getVdnPhone(anyString());
    }

    @Test void managedContextWithoutSipAuthenticationIsRejected() {
        FsParkHandler handler = new FsParkHandler(); inject(handler);
        when(cache.getCallInfo("untrusted")).thenReturn(null);
        FsParkEvent event = new FsParkEvent(); event.setDeviceId("untrusted"); event.setDirection("inbound");
        event.setAgentCall("true"); event.setRemoteAddress("fs:8021"); event.setCaller("1001");
        handler.handleEvent(event);
        verify(fs).hangupCall("fs:8021",null,"untrusted");
        verifyNoInteractions(agents);
    }

    @Test void unauthenticatedTrunkStillUsesVdnRouting() {
        FsParkHandler handler = new FsParkHandler(); inject(handler);
        ReflectionTestUtils.setField(handler,"snowflakeIdWorker",new com.voxai.core.util.SnowflakeIdWorker(0,0));
        when(cache.getCallInfo("trunk")).thenReturn(null);
        FsParkEvent event = new FsParkEvent(); event.setDeviceId("trunk"); event.setDirection("inbound");
        event.setCalled("9999"); event.setRemoteAddress("fs:8021");
        handler.handleEvent(event);
        verify(cache).getVdnPhone("9999");
        verify(cdr).saveOrUpdateCallLog(argThat(log->"INBOUND_CALL".equals(log.getCallType())));
    }

    @Test void callerAnswerAssignsCalleeDeviceToCalleeAgent() {
        group.setRecordType(0);
        call.setCalledAgentKey("b"); call.setCalled("1002"); call.setCalledDisplay("1001");
        call.getDeviceList().remove("db"); call.getDeviceInfoMap().remove("db");
        call.getNextCommands().add(new NextCommand("da",NextType.NEXT_CALL_OTHER,null));
        b.setLoginType(2); b.setSips(Collections.singletonList("1002")); b.setCompanyId(1L);
        when(cache.getRouteGetway(1L,"1002")).thenReturn(new com.voxai.core.entity.RouteGetway());
        FsAnswerHandler handler = new FsAnswerHandler(); inject(handler);
        FsAnswerEvent event = new FsAnswerEvent(); event.setDeviceId("da"); event.setTimestamp(1700000003000000L);
        handler.handleEvent(event);
        DeviceInfo callee = call.getDeviceInfoMap().values().stream().filter(d->!d.getDeviceId().equals("da")).findFirst().get();
        assertEquals("b",callee.getAgentKey()); assertEquals(3,callee.getCdrType()); assertEquals(1,callee.getDeviceType());
        assertEquals(callee.getDeviceId(),b.getDeviceId());
    }

    @Test void failedCalleeRouteDoesNotLeavePhantomDevicePreventingCleanup() {
        group.setRecordType(0);
        call.setCalledAgentKey("b"); call.setCalled("1002");
        call.getDeviceList().remove("db"); call.getDeviceInfoMap().remove("db");
        call.getNextCommands().add(new NextCommand("da",NextType.NEXT_CALL_OTHER,null));
        b.setLoginType(2); b.setSips(Collections.singletonList("1002")); b.setCompanyId(1L);
        FsAnswerHandler handler = new FsAnswerHandler(); inject(handler);
        FsAnswerEvent event = new FsAnswerEvent(); event.setDeviceId("da"); event.setTimestamp(1700000003000000L);
        handler.handleEvent(event);
        assertEquals(Collections.singletonList("da"),call.getDeviceList());
        verify(fs).hangupCall("fs:8021",900L,"da");
    }

    @Test void finalHangupRestoresBothAgentsAndPersistsInnerCdr() {
        call.setCalledAgentKey("b"); call.getAgentStatesBeforeCall().put("a",AgentState.READY);
        call.getAgentStatesBeforeCall().put("b",AgentState.NOT_READY);
        call.getDeviceList().remove("da"); da.setEndTime(1700000010000L);
        FsHangupCompleteHandler handler = new FsHangupCompleteHandler(); inject(handler);
        FsHangupCompleteEvent event = new FsHangupCompleteEvent(); event.setDeviceId("db");
        event.setTimestamp(1700000011000000L);
        handler.handleEvent(event);
        assertNull(a.getCallId()); assertNull(b.getCallId());
        assertEquals(AgentState.READY,a.getAgentState()); assertEquals(AgentState.NOT_READY,b.getAgentState());
        verify(cdr).completeCall(argThat(snapshot->"INNER_CALL".equals(snapshot.getCallLog().getCallType())));
        verify(cache).removeCallInfo(900L);
    }

    @Test void oldHangupDoesNotClearCalleeNewCall() {
        call.setCalledAgentKey("b"); b.setCallId(901L); b.setDeviceId("new-device");
        call.getDeviceList().remove("da"); da.setEndTime(1700000010000L);
        FsHangupCompleteHandler handler = new FsHangupCompleteHandler(); inject(handler);
        FsHangupCompleteEvent event = new FsHangupCompleteEvent(); event.setDeviceId("db"); event.setTimestamp(1700000011000000L);
        handler.handleEvent(event);
        assertEquals(901L,b.getCallId()); assertEquals("new-device",b.getDeviceId());
        verify(cache,never()).addAgentInfo(b);
    }

    @Test void originFailureBeforeCalleeOriginationStillReleasesBothAgents() {
        call.setCalledAgentKey("b"); call.getAgentStatesBeforeCall().put("a",AgentState.READY);
        call.getAgentStatesBeforeCall().put("b",AgentState.NOT_READY);
        call.getDeviceList().remove("db"); call.getDeviceInfoMap().remove("db");
        FsHangupCompleteHandler handler = new FsHangupCompleteHandler(); inject(handler);
        FsHangupCompleteEvent event = new FsHangupCompleteEvent(); event.setDeviceId("da"); event.setTimestamp(1700000011000000L);
        handler.handleEvent(event);
        assertNull(b.getCallId()); assertNull(b.getDeviceId()); assertEquals(AgentState.NOT_READY,b.getAgentState());
        verify(cache).removeCallInfo(900L);
    }

    @Test void outboundHangupClearsCallerEvenWhenDelayedStateMessageLostCallId() {
        call.setCallType(CallType.OUTBOUNT_CALL);
        db.setDeviceType(2); db.setCdrType(2);
        da.setCdrType(2);
        a.setCallId(null); a.setDeviceId("da"); a.setAgentState(AgentState.TALKING);
        call.getDeviceList().remove("db"); db.setEndTime(1700000010000L);
        FsHangupCompleteHandler handler = new FsHangupCompleteHandler(); inject(handler);
        FsHangupCompleteEvent event = new FsHangupCompleteEvent(); event.setDeviceId("da"); event.setTimestamp(1700000011000000L);
        handler.handleEvent(event);
        assertEquals(AgentState.NOT_READY,a.getAgentState()); assertNull(a.getDeviceId());
        verify(ws).sendMessgae(eq(a),argThat(json -> "NOT_READY".equals(JSON.parseObject(json).getJSONObject("data").getString("agentState"))));
    }

    @Test void outboundToAgentReleasesBothParticipantsEvenThoughCalleeDeviceIsClassifiedAsCustomer() {
        call.setCallType(CallType.OUTBOUNT_CALL); call.setCalledAgentKey("b");
        da.setCdrType(2); db.setCdrType(2); db.setDeviceType(2);
        a.setAgentState(AgentState.TALKING);
        call.getDeviceList().remove("db"); db.setEndTime(1700000010000L);
        FsHangupCompleteHandler handler = new FsHangupCompleteHandler(); inject(handler);
        FsHangupCompleteEvent event = new FsHangupCompleteEvent(); event.setDeviceId("da"); event.setTimestamp(1700000011000000L);
        handler.handleEvent(event);
        assertNull(a.getCallId()); assertNull(b.getCallId());
        assertEquals(AgentState.NOT_READY,a.getAgentState()); assertEquals(AgentState.NOT_READY,b.getAgentState());
        verify(ws).sendMessgae(eq(a),contains("\"type\":\"AFTER\""));
        verify(ws).sendMessgae(eq(b),contains("\"type\":\"AFTER\""));
    }
}
