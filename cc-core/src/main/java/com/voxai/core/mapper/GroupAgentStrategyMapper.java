package com.voxai.core.mapper;

import com.voxai.core.entity.GroupAgentStrategy;
import com.voxai.core.mapper.base.BaseMapper;
import com.voxai.core.po.GroupAgentStrategyPo;

public interface GroupAgentStrategyMapper extends BaseMapper<GroupAgentStrategy> {

    /**
     * 技能组中坐席空闲策略
     * @param groupId
     * @return
     */
    GroupAgentStrategyPo selectByGroupId(Long groupId);
}