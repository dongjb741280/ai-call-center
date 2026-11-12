package com.voxai.cc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.voxai.cc.cache.CacheService;
import com.voxai.cc.entity.MakeCallVo;
import com.voxai.cc.exception.BusinessException;
import com.voxai.cc.fs.FsListen;
import com.voxai.cc.service.AgentService;
import com.voxai.cc.websocket.WebSocketHandler;
import com.voxai.core.entity.RouteGetway;
import com.voxai.core.enums.CallType;
import com.voxai.core.enums.ErrorCode;
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

class CallCdrMakeCallTest {
    private CallCdrServiceImpl service;
    private CacheService cache;
    private WebSocketHandler websocket;
    private FsListen fs;
    private AgentInfo agent;
    private MakeCallVo request;

    @BeforeEach void setup() {
        service = new CallCdrServiceImpl();
        cache = mock(CacheService.class);
        websocket = mock(WebSocketHandler.class);
        fs = mock(FsListen.class);
        ReflectionTestUtils.setField(service, "cacheService", cache);
        ReflectionTestUtils.setField(service, "webSocketHandler", websocket);
        ReflectionTestUtils.setField(service, "fsListen", fs);
        ReflectionTestUtils.setField(service, "agentService", mock(AgentService.class));
        ReflectionTestUtils.setField(service, "snowflakeIdWorker", new SnowflakeIdWorker(0, 0));
        agent = new AgentInfo();
        agent.setAgentKey("agent1001");
        agent.setAgentId("1001");
        agent.setCompanyId(1L);
        agent.setGroupId(25L);
        agent.setLoginType(2);
        agent.setSips(Collections.singletonList("1001"));
        agent.setAgentState(AgentState.READY);
        GroupInfo group = new GroupInfo();
        group.setCalledDisplays(Collections.singletonList("1001"));
        when(cache.getGroupInfo(25L)).thenReturn(group);
        CompanyInfo company = new CompanyInfo();
        company.setHiddenCustomer(0);
        when(cache.getCompany(1L)).thenReturn(company);
        request = new MakeCallVo();
        request.setCalled("1005");
        request.setCallType(CallType.OUTBOUNT_CALL);
    }

    @Test void missingOriginRouteReturnsBusinessErrorWithoutLeakingCallState() {
        BusinessException error = assertThrows(BusinessException.class, () -> service.makeCall(request, agent));
        assertEquals(ErrorCode.CALL_ROUTE_ERROR.getCode(), error.getErrorCode());
        assertEquals(AgentState.READY, agent.getAgentState());
        assertNull(agent.getCallId());
        verify(cache, never()).addCallInfo(any());
        verify(cache, never()).addDevice(anyString(), anyLong());
        verifyNoInteractions(websocket);
        verifyNoInteractions(fs);
    }

    @Test void successfulOriginationSendsJsonInsteadOfAnEmptyFrame() {
        RouteGetway gateway = new RouteGetway();
        when(cache.getRouteGetway(1L, "1001")).thenReturn(gateway);
        assertEquals(0, service.makeCall(request, agent));
        JSONObject response = response();
        assertEquals(0, response.getIntValue("code"));
        assertEquals("OUT_CALL", response.getString("type"));
        assertEquals(AgentState.OUT_CALL, agent.getAgentState());
        verify(fs).makeCall(eq(gateway), eq("1001"), eq("1001"), anyLong(), anyString(), isNull(), isNull());
    }

    private JSONObject response() {
        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
        verify(websocket).sendMessgae(eq(agent), payload.capture());
        assertFalse(payload.getValue().isEmpty(), "WebSocket replies must contain JSON");
        return JSON.parseObject(payload.getValue());
    }
}
