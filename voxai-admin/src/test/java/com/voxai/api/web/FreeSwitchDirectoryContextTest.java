package com.voxai.api.web;

import com.voxai.core.entity.Agent;
import com.voxai.core.entity.AgentSip;
import com.voxai.core.mapper.AgentMapper;
import com.voxai.core.mapper.AgentSipMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FreeSwitchDirectoryContextTest {
    @Test void boundAgentUsesManagedContextAndUnboundExtensionKeepsDefault() {
        FreeSwitchDirectoryController controller = new FreeSwitchDirectoryController();
        AgentSipMapper sips = mock(AgentSipMapper.class);
        AgentMapper agents = mock(AgentMapper.class);
        ReflectionTestUtils.setField(controller,"agentSipMapper",sips);
        ReflectionTestUtils.setField(controller,"agentMapper",agents);
        AgentSip sip = new AgentSip(); sip.setSip(1002); sip.setCompanyId(1L); sip.setSipPwd("test");
        when(sips.selectBySip(1002)).thenReturn(sip);
        assertTrue(controller.directory("1002","localhost").contains("name=\"user_context\" value=\"default\""));
        when(agents.selectAgentBySip("1002")).thenReturn(new Agent());
        assertTrue(controller.directory("1002","localhost").contains("name=\"user_context\" value=\"voxai-agent\""));
        ReflectionTestUtils.setField(controller,"agentContext","default");
        assertTrue(controller.directory("1002","localhost").contains("name=\"user_context\" value=\"default\""));
        assertFalse(controller.directory("missing","localhost").contains("<user id="));
    }
}
