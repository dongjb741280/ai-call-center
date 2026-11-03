package com.voxai.api.service.impl;

import com.voxai.api.exception.BusinessException;
import com.voxai.api.service.AgentService;
import com.voxai.api.util.BcryptUtil;
import com.voxai.core.entity.*;
import com.voxai.core.mapper.*;
import com.voxai.core.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AgentEditorServiceTest {
    private final AgentMapper agents = mock(AgentMapper.class);
    private final AgentGroupMapper groups = mock(AgentGroupMapper.class);
    private final GroupMapper groupMapper = mock(GroupMapper.class);
    private final SkillMapper skills = mock(SkillMapper.class);
    private final SkillAgentMapper skillAgents = mock(SkillAgentMapper.class);
    private final CompanyMapper companies = mock(CompanyMapper.class);
    private final AgentServiceImpl service = new AgentServiceImpl();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "agentMapper", agents);
        ReflectionTestUtils.setField(service, "agentGroupMapper", groups);
        ReflectionTestUtils.setField(service, "groupMapper", groupMapper);
        ReflectionTestUtils.setField(service, "skillMapper", skills);
        ReflectionTestUtils.setField(service, "skillAgentMapper", skillAgents);
        ReflectionTestUtils.setField(service, "companyMapper", companies);
        Company company = new Company();
        company.setCompanyCode("example");
        company.setAgentLimit(100);
        when(companies.selectByPrimaryKey(2L)).thenReturn(company);
        Agent agent = new Agent();
        agent.setId(7L);
        agent.setAgentId("1001");
        agent.setAgentKey("1001@example");
        agent.setCompanyId(2L);
        agent.setAgentType(1);
        when(agents.selectForGroupUpdate(2L, 7L)).thenReturn(agent);
        Group group = new Group();
        group.setCompanyId(2L);
        group.setStatus(1);
        when(groupMapper.selectByPrimaryKey(10L)).thenReturn(group);
        when(skills.selectById(2L, 30L)).thenReturn(new Skill());
    }

    @Test
    void savesBaseGroupsAndSkillsTogetherWithoutResettingUnchangedPassword() {
        AgentVo request = request();
        request.setGroups(groups());
        request.setSkillAgents(Collections.singletonList(skill(30L, 50)));
        request.setDisplay("12345");
        request.setRingTime(40);
        service.saveOrUpdate(request);

        ArgumentCaptor<Agent> update = ArgumentCaptor.forClass(Agent.class);
        verify(agents).updateByPrimaryKeySelective(update.capture());
        assertNull(update.getValue().getPasswd());
        assertEquals("12345", update.getValue().getDisplay());
        assertEquals(Integer.valueOf(40), update.getValue().getRingTime());
        verify(groups).deleteByAgent(7L);
        verify(skillAgents).deleteByAgent(2L, 7L);
        ArgumentCaptor<SkillAgent> record = ArgumentCaptor.forClass(SkillAgent.class);
        verify(skillAgents).insert(record.capture());
        assertEquals(Long.valueOf(30), record.getValue().getSkillId());
        assertEquals(Integer.valueOf(50), record.getValue().getRankValue());
        assertEquals(Long.valueOf(2), record.getValue().getCompanyId());
    }

    @Test
    void omittedRelationsPreserveExistingDataWhileEmptyListsRemoveIt() {
        service.saveOrUpdate(request());
        verifyNoInteractions(groups, skillAgents);
        AgentVo clear = request();
        clear.setGroups(new AgentGroupsVo());
        clear.getGroups().setGroupIds(Collections.emptyList());
        clear.setSkillAgents(Collections.emptyList());
        service.saveOrUpdate(clear);
        verify(groups).deleteByAgent(7L);
        verify(skillAgents).deleteByAgent(2L, 7L);
        verify(skillAgents, never()).insert(any());
    }

    @Test
    void rejectsDuplicateOutOfRangeAndOtherTenantSkills() {
        for (java.util.List<AgentSkillVo> invalid : Arrays.asList(
                Arrays.asList(skill(30L, 10), skill(30L, 20)),
                Collections.singletonList(skill(30L, 101)),
                Collections.singletonList(skill(99L, 50)))) {
            AgentVo request = request();
            request.setSkillAgents(invalid);
            assertThrows(BusinessException.class, () -> service.saveOrUpdate(request));
        }
        verify(skillAgents, never()).deleteByAgent(anyLong(), anyLong());
        verify(agents, never()).updateByPrimaryKeySelective(any());
    }

    @Test
    void rollsBackGroupChangesWhenSkillValidationFails() {
        PlatformTransactionManager manager = mock(PlatformTransactionManager.class);
        TransactionStatus status = mock(TransactionStatus.class);
        when(manager.getTransaction(any())).thenReturn(status);
        ProxyFactory factory = new ProxyFactory(service);
        factory.addAdvice(new TransactionInterceptor(manager, new AnnotationTransactionAttributeSource()));
        AgentService proxy = (AgentService) factory.getProxy();
        AgentVo request = request();
        request.setGroups(groups());
        request.setSkillAgents(Collections.singletonList(skill(99L, 50)));
        assertThrows(BusinessException.class, () -> proxy.saveOrUpdate(request));
        verify(groups).deleteByAgent(7L);
        verify(manager).rollback(status);
        verify(manager, never()).commit(any());
    }

    @Test
    void createsWithHashedPasswordAndGeneratedIdBeforeSavingRelations() {
        AgentVo request = request();
        request.setId(null);
        request.setPasswd(String.join("", Collections.nCopies(64, "a")));
        request.setGroups(groups());
        request.setSkillAgents(Collections.singletonList(skill(30L, 50)));
        when(agents.selectListByMap(anyMap())).thenReturn(Collections.emptyList());
        when(agents.selectCountByMap(anyMap())).thenReturn(0);
        when(agents.insertSelective(any())).thenAnswer(invocation -> {
            Agent inserted = invocation.getArgument(0);
            assertTrue(BcryptUtil.checkPwd(request.getPasswd(), inserted.getPasswd()));
            inserted.setId(7L);
            return 1;
        });
        assertEquals(1, service.saveOrUpdate(request));
        verify(groups).deleteByAgent(7L);
        verify(skillAgents).deleteByAgent(2L, 7L);
    }

    private AgentVo request() {
        AgentVo request = new AgentVo();
        request.setId(7L);
        request.setCompanyId(2L);
        request.setAgentId("1001");
        request.setAgentName("客服一");
        return request;
    }

    private AgentGroupsVo groups() {
        AgentGroupsVo request = new AgentGroupsVo();
        request.setGroupIds(Collections.singletonList(10L));
        request.setGroupId(10L);
        return request;
    }

    private AgentSkillVo skill(Long id, int rank) {
        AgentSkillVo skill = new AgentSkillVo();
        skill.setSkillId(id);
        skill.setRankValue(rank);
        return skill;
    }
}
