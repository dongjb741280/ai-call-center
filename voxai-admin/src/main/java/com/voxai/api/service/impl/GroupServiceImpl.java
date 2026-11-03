/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.api.service.impl;


import com.voxai.core.entity.*;
import com.voxai.core.mapper.*;
import com.voxai.core.enums.ErrorCode;
import com.voxai.core.mapper.base.BaseMapper;
import com.voxai.core.po.GroupInfo;
import com.voxai.core.vo.GroupInfoVo;
import com.voxai.core.vo.GroupSkillsVo;
import com.voxai.core.vo.SkillGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.voxai.api.exception.BusinessException;
import com.voxai.api.service.GroupService;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GroupServiceImpl extends BaseServiceImpl<Group> implements GroupService {

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private AgentGroupMapper agentGroupMapper;

    @Autowired
    private SkillGroupMapper skillGroupMapper;

    @Autowired
    private SkillMapper skillMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private VdnConfigMapper vdnScheduleMapper;

    @Override
    BaseMapper<Group> baseMapper() {
        return groupMapper;
    }

    @Override
    public GroupInfo getGroupInfo(Long companyId, Long id) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("companyId", companyId);
        params.put("id", id);
        return groupMapper.selectGroupInfo(params);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveOrUpdateGroup(GroupInfoVo groupInfoVo) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("companyId", groupInfoVo.getCompanyId());
        params.put("name", groupInfoVo.getName());
        List<Group> groups = groupMapper.selectListByMap(params);
        //判断是否重复
        if (!CollectionUtils.isEmpty(groups)) {
            if (groupInfoVo.getId() == null ||
                    (groupInfoVo.getId() != null && !groupInfoVo.getId().equals(groups.get(0).getId()))) {
                throw new BusinessException(ErrorCode.DUPLICATE_EXCEPTION);
            }
        }

        validateSkills(groupInfoVo);
        Group group = new Group();
        BeanUtils.copyProperties(groupInfoVo, group);
        if (groupInfoVo.getId() == null) {
            //判断技能组是否超过限制
            Company company = companyMapper.selectByPrimaryKey(groupInfoVo.getCompanyId());
            params.remove("name");
            Integer count = groupMapper.selectCountByMap(params);
            if (count > company.getGroupLimit()) {
                throw new BusinessException(ErrorCode.GROUP_OVER_LIMIT);
            }
            group.setCts(Instant.now().getEpochSecond());
            groupMapper.insertSelective(group);
            Long id = groupMapper.selectByName(groupInfoVo.getCompanyId(), groupInfoVo.getName());
            group.setId(id);
            saveGroupSkills(groupInfoVo, null);

            //坐席空闲策略

            return 1;
        }

        params.put("id", groupInfoVo.getId());
        GroupInfo exist = groupMapper.selectGroupInfo(params);
        if (exist == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST);
        }

        group.setUts(Instant.now().getEpochSecond());
        groupMapper.updateByPrimaryKeySelective(group);
        saveGroupSkills(groupInfoVo, skillGroupMapper.selectByGroup(groupInfoVo.getCompanyId(), groupInfoVo.getId()));
        //排队策略


        //溢出策略

        return 0;
    }

    private void validateSkills(GroupInfoVo groupInfoVo) {
        validateSkills(groupInfoVo.getCompanyId(), groupInfoVo.getSkillList());
    }

    private void validateSkills(Long companyId, List<SkillGroupVo> skills) {
        if (skills == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "技能组技能不能为空");
        }
        if (skills.size() > 10) {
            throw new BusinessException(ErrorCode.GROUP_SKILL_OVER_LIMIT);
        }
        Set<Long> skillIds = new HashSet<>();
        for (SkillGroupVo skill : skills) {
            if (skill.getSkillId() == null || !skillIds.add(skill.getSkillId())) {
                throw new BusinessException(ErrorCode.PARAMETER_ERROR, "技能组技能缺失或重复");
            }
            if (skillMapper.selectById(companyId, skill.getSkillId()) == null) {
                throw new BusinessException(ErrorCode.DATA_NOT_EXIST, "技能不存在或已停用");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveGroupSkills(Long companyId, Long groupId, GroupSkillsVo groupSkillsVo) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("companyId", companyId);
        params.put("id", groupId);
        if (groupMapper.selectGroupInfo(params) == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST, "技能组不存在");
        }
        validateSkills(companyId, groupSkillsVo.getSkillList());
        GroupInfoVo group = new GroupInfoVo();
        group.setId(groupId);
        group.setCompanyId(companyId);
        group.setSkillList(groupSkillsVo.getSkillList());
        saveGroupSkills(group, skillGroupMapper.selectByGroup(companyId, groupId));
        return groupSkillsVo.getSkillList().size();
    }

    private void saveGroupSkills(GroupInfoVo groupInfoVo, List<SkillGroup> existingSkills) {
        long now = Instant.now().getEpochSecond();
        Map<Long, SkillGroup> existingBySkillId = new HashMap<>();
        if (existingSkills != null) {
            for (SkillGroup existing : existingSkills) {
                existingBySkillId.put(existing.getSkillId(), existing);
            }
        }
        for (SkillGroupVo skillVo : groupInfoVo.getSkillList()) {
            SkillGroup skillGroup = existingBySkillId.remove(skillVo.getSkillId());
            boolean insert = skillGroup == null;
            if (insert) {
                skillGroup = new SkillGroup();
                skillGroup.setCts(now);
                skillGroup.setCompanyId(groupInfoVo.getCompanyId());
                skillGroup.setGroupId(groupInfoVo.getId());
                skillGroup.setSkillId(skillVo.getSkillId());
            }
            BeanUtils.copyProperties(skillVo, skillGroup);
            skillGroup.setStatus(1);
            skillGroup.setUts(now);
            if (insert) {
                skillGroupMapper.insertSelective(skillGroup);
            } else {
                skillGroupMapper.updateByPrimaryKeySelective(skillGroup);
            }
        }
        for (SkillGroup removed : existingBySkillId.values()) {
            skillGroupMapper.deleteByPrimaryKey(removed.getId());
        }
    }

    @Override
    public int deleteGroup(Long companyId, Long id) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("companyId", companyId);
        params.put("id", id);
        List<Group> groups = groupMapper.selectListByMap(params);
        //是否存在
        if (CollectionUtils.isEmpty(groups)) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST);
        }
        //技能组绑定了不能删
        params.put("routeType", 1);
        params.put("routeValue", id);
        List<VdnConfig> vdnSchedules = vdnScheduleMapper.selectListByMap(params);
        if (!CollectionUtils.isEmpty(vdnSchedules)) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST, "技能组已经被vdn引用");
        }

        //删除当前技能组与坐席关系
        agentGroupMapper.deleteByGroup(id);

        Group group = new Group();
        group.setId(groups.get(0).getId());
        group.setStatus(0);
        group.setUts(Instant.now().getEpochSecond());
        group.setName(groups.get(0).getName() + randomDelete());
        return groupMapper.updateByPrimaryKeySelective(group);
    }

    @Override
    public List<AgentGroup> groupAgentList(Long companyId, Long groupId) {
        Map<String, Object> params = new HashMap<>();
        params.put("companyId", companyId);
        params.put("groupId", groupId);
        return agentGroupMapper.selectListByMap(params);
    }
}
