package com.voxai.api.service.impl;

import com.voxai.api.exception.BusinessException;
import com.voxai.core.entity.Agent;
import com.voxai.core.entity.AgentGroup;
import com.voxai.core.entity.Group;
import com.voxai.core.mapper.AgentGroupMapper;
import com.voxai.core.mapper.AgentMapper;
import com.voxai.core.mapper.GroupMapper;
import com.voxai.core.vo.AgentGroupsVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AgentGroupsServiceTest {
    private final AgentMapper agentMapper = mock(AgentMapper.class);
    private final AgentGroupMapper relations = mock(AgentGroupMapper.class);
    private final GroupMapper groupMapper = mock(GroupMapper.class);
    private final AgentServiceImpl service = new AgentServiceImpl();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "agentMapper", agentMapper);
        ReflectionTestUtils.setField(service, "agentGroupMapper", relations);
        ReflectionTestUtils.setField(service, "groupMapper", groupMapper);
        Agent agent = new Agent();
        agent.setId(7L);
        agent.setCompanyId(2L);
        agent.setAgentKey("1001@example");
        agent.setAgentType(1);
        when(agentMapper.selectForGroupUpdate(2L, 7L)).thenReturn(agent);
        for (Long id : Arrays.asList(10L, 20L)) {
            Group group = new Group();
            group.setId(id);
            group.setCompanyId(2L);
            group.setStatus(1);
            when(groupMapper.selectByPrimaryKey(id)).thenReturn(group);
        }
    }

    @Test
    void savesAllMembershipsWithExactlyOnePrimary() {
        assertEquals(2, service.saveAgentGroups(2L, 7L, request(Arrays.asList(10L, 20L), 20L)));

        ArgumentCaptor<AgentGroup> records = ArgumentCaptor.forClass(AgentGroup.class);
        verify(relations).deleteByAgent(7L);
        verify(relations, times(2)).insertSelective(records.capture());
        assertEquals(Integer.valueOf(0), records.getAllValues().get(0).getIsPrimary());
        AgentGroup primary = records.getAllValues().get(1);
        assertEquals(Long.valueOf(20), primary.getGroupId());
        assertEquals(Integer.valueOf(1), primary.getIsPrimary());
        assertEquals(Long.valueOf(7), primary.getAgentId());
        assertEquals(Long.valueOf(2), primary.getCompanyId());
        assertEquals("1001@example", primary.getAgentKey());
        assertEquals(Integer.valueOf(1), primary.getStatus());
    }

    @Test
    void rejectsInvalidPrimaryOrMembershipsBeforeDeletingAnything() {
        for (AgentGroupsVo invalid : Arrays.asList(
                request(Arrays.asList(10L, 20L), null),
                request(Collections.singletonList(10L), 20L),
                request(Arrays.asList(10L, 10L), 10L),
                request(Arrays.asList(10L, null), 10L),
                request(null, null),
                request(Collections.emptyList(), 10L))) {
            assertThrows(BusinessException.class, () -> service.saveAgentGroups(2L, 7L, invalid));
        }
        verifyNoInteractions(relations);
    }

    @Test
    void rejectsOtherTenantAgent() {
        assertThrows(BusinessException.class,
                () -> service.saveAgentGroups(3L, 7L, request(Collections.singletonList(10L), 10L)));
        verifyNoInteractions(relations, groupMapper);
    }

    @Test
    void rejectsOtherTenantDisabledAndMissingGroupsBeforeDeletingAnything() {
        Group group = groupMapper.selectByPrimaryKey(10L);
        group.setCompanyId(3L);
        assertThrows(BusinessException.class,
                () -> service.saveAgentGroups(2L, 7L, request(Collections.singletonList(10L), 10L)));
        group.setCompanyId(2L);
        group.setStatus(0);
        assertThrows(BusinessException.class,
                () -> service.saveAgentGroups(2L, 7L, request(Collections.singletonList(10L), 10L)));
        assertThrows(BusinessException.class,
                () -> service.saveAgentGroups(2L, 7L, request(Collections.singletonList(99L), 99L)));
        verifyNoInteractions(relations);
    }

    @Test
    void allowsRemovingAllMembershipsWithoutLeavingAPrimary() {
        assertEquals(0, service.saveAgentGroups(2L, 7L, request(Collections.emptyList(), null)));
        verify(relations).deleteByAgent(7L);
        verify(relations, never()).insertSelective(any());
    }

    private AgentGroupsVo request(List<Long> ids, Long primary) {
        AgentGroupsVo request = new AgentGroupsVo();
        request.setGroupIds(ids);
        request.setGroupId(primary);
        return request;
    }
}
