package com.voxai.cc.dialer;

import com.voxai.cc.cache.CacheService;
import com.voxai.cc.command.GroupHandler;
import com.voxai.cc.service.AgentService;
import com.voxai.cc.websocket.WebSocketHandler;
import com.voxai.core.po.*;
import org.junit.jupiter.api.*;
import java.time.Instant;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskAfterServiceTest {
    @Test void redisDeadlineSurvivesServiceRestartAndDoesNotOverrideManualNotReady() {
        CacheService cache=mock(CacheService.class); GroupHandler groups=mock(GroupHandler.class); AgentService agents=mock(AgentService.class);
        DialerControlProperties properties=new DialerControlProperties(); properties.setTokens(Collections.singletonMap(7L,"token"));
        AgentInfo agent=new AgentInfo(); agent.setCompanyId(7L); agent.setStatus(1); agent.setAgentKey("agent"); agent.setAgentState(AgentState.TALKING); agent.setLogoutTime(0L); agent.setGroupIds(Collections.singletonList(1L));
        TaskAfterService first=new TaskAfterService(cache,groups,properties,agents,mock(WebSocketHandler.class));
        CallInfo call=new CallInfo(); call.setTaskAfterDuration(60); first.begin(call,agent); assertEquals(AgentState.AFTER,agent.getAgentState()); assertTrue(agent.getTaskAfterUntil()>Instant.now().getEpochSecond());
        CompanyInfo owner=new CompanyInfo(); owner.setGroupIds(Collections.singletonList(1L)); GroupInfo group=new GroupInfo(); group.setOnlineAgents(Collections.singletonList("agent"));
        when(cache.getCompany(7L)).thenReturn(owner); when(cache.getGroupInfo(1L)).thenReturn(group); when(cache.getAgentInfo("agent")).thenReturn(agent);
        TaskAfterService restarted=new TaskAfterService(cache,groups,properties,agents,mock(WebSocketHandler.class));
        restarted.tick(); verify(groups,never()).agentFree(any());
        agent.setTaskAfterUntil(Instant.now().getEpochSecond()-1); restarted.tick(); assertEquals(AgentState.READY,agent.getAgentState()); verify(groups).agentFree(agent);
        clearInvocations(groups); agent.setAgentState(AgentState.NOT_READY); agent.setTaskAfterUntil(Instant.now().getEpochSecond()-1); restarted.tick(); assertEquals(AgentState.NOT_READY,agent.getAgentState()); verify(groups,never()).agentFree(any());
    }
}
