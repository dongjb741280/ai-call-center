package com.voxai.cc.listen;

import com.alibaba.fastjson.JSON;
import com.voxai.cc.cache.CacheService;
import com.voxai.core.po.AgentInfo;
import com.voxai.core.po.AgentState;
import com.voxai.cc.websocket.response.AgentStateResppnse;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AgentStateOrderingTest {
    @Test void delayedLoginCannotEraseActiveCallOwnership() {
        AgentStateListen listener = new AgentStateListen();
        CacheService cache = mock(CacheService.class);
        ReflectionTestUtils.setField(listener,"cacheService",cache);
        ReflectionTestUtils.setField(listener,"appId","local");
        AgentInfo current = new AgentInfo(); current.setAgentKey("agent1002");
        current.setHost("host"); current.setCallId(900L); current.setDeviceId("da");
        current.setAgentState(AgentState.TALKING); current.setStateTime(200L);
        when(cache.getAgentInfo("agent1002")).thenReturn(current);
        AgentStateResppnse stale = new AgentStateResppnse(); stale.setAgentKey("agent1002");
        stale.setAppId("other"); stale.setHost("host"); stale.setAgentState(AgentState.LOGIN); stale.setStateTime(100L);
        listener.listenAgentState(JSON.toJSONString(stale));
        assertEquals(900L,current.getCallId()); assertEquals(AgentState.TALKING,current.getAgentState());
        verify(cache,never()).addAgentInfo(any());
    }
}
