package com.voxai.api.service.impl;

import com.voxai.api.exception.BusinessException;
import com.voxai.core.entity.Group;
import com.voxai.core.entity.Skill;
import com.voxai.core.entity.SkillGroup;
import com.voxai.core.po.GroupInfo;
import com.voxai.core.mapper.GroupMapper;
import com.voxai.core.mapper.SkillGroupMapper;
import com.voxai.core.mapper.SkillMapper;
import com.voxai.core.vo.GroupSkillsVo;
import com.voxai.core.vo.SkillGroupVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GroupSkillServiceTest {
    private final GroupMapper groups = mock(GroupMapper.class);
    private final SkillMapper skills = mock(SkillMapper.class);
    private final SkillGroupMapper relations = mock(SkillGroupMapper.class);
    private final GroupServiceImpl service = new GroupServiceImpl();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "groupMapper", groups);
        ReflectionTestUtils.setField(service, "skillMapper", skills);
        ReflectionTestUtils.setField(service, "skillGroupMapper", relations);
        Group group = new Group();
        group.setId(10L);
        group.setCompanyId(2L);
        group.setStatus(1);
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setId(10L);
        groupInfo.setCompanyId(2L);
        when(groups.selectGroupInfo(any(Map.class))).thenReturn(groupInfo);
        for (long id : Arrays.asList(30L, 40L, 50L)) {
            Skill skill = new Skill();
            skill.setId(id);
            skill.setCompanyId(2L);
            skill.setStatus(1);
            when(skills.selectById(2L, id)).thenReturn(skill);
        }
    }

    @Test
    void updatesRulesAddsSkillsAndRemovesOmittedBindings() {
        SkillGroup existing = relation(101L, 30L);
        SkillGroup removed = relation(102L, 40L);
        when(relations.selectByGroup(2L, 10L)).thenReturn(Arrays.asList(existing, removed));

        assertEquals(2, service.saveGroupSkills(2L, 10L, request(rule(30L, 80), rule(50L, 60))));

        verify(relations).updateByPrimaryKeySelective(existing);
        assertEquals(Integer.valueOf(80), existing.getShareValue());
        verify(relations).deleteByPrimaryKey(102L);
        ArgumentCaptor<SkillGroup> inserted = ArgumentCaptor.forClass(SkillGroup.class);
        verify(relations).insertSelective(inserted.capture());
        assertEquals(Long.valueOf(50), inserted.getValue().getSkillId());
        assertEquals(Long.valueOf(10), inserted.getValue().getGroupId());
        assertEquals(Long.valueOf(2), inserted.getValue().getCompanyId());
    }

    @Test
    void emptyListRemovesAllBindings() {
        when(relations.selectByGroup(2L, 10L)).thenReturn(Arrays.asList(relation(101L, 30L), relation(102L, 40L)));
        assertEquals(0, service.saveGroupSkills(2L, 10L, request()));
        verify(relations).deleteByPrimaryKey(101L);
        verify(relations).deleteByPrimaryKey(102L);
        verify(relations, never()).insertSelective(any(SkillGroup.class));
    }

    @Test
    void rejectsForeignTenantSkillsBeforeChangingRelations() {
        assertThrows(BusinessException.class, () -> service.saveGroupSkills(2L, 10L, request(rule(999L, 50))));
        verifyNoInteractions(relations);
    }

    private GroupSkillsVo request(SkillGroupVo... values) {
        GroupSkillsVo request = new GroupSkillsVo();
        request.setSkillList(Arrays.asList(values));
        return request;
    }

    private SkillGroupVo rule(Long skillId, int shareValue) {
        SkillGroupVo rule = new SkillGroupVo();
        rule.setSkillId(skillId);
        rule.setLevelValue(1);
        rule.setRankType(0);
        rule.setRankValue(1);
        rule.setRankValueStart(1);
        rule.setMatchType(1);
        rule.setShareValue(shareValue);
        return rule;
    }

    private SkillGroup relation(Long id, Long skillId) {
        SkillGroup relation = new SkillGroup();
        relation.setId(id);
        relation.setSkillId(skillId);
        relation.setGroupId(10L);
        relation.setCompanyId(2L);
        relation.setStatus(1);
        return relation;
    }
}
