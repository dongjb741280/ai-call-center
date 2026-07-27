package com.voxai.core.strategy;

import com.voxai.core.po.AgentInfo;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public interface AgentStrategy {

    /**
     * 坐席空闲策略
     *
     * @param agentInfo
     * @return
     */
    Long calculateLevel(AgentInfo agentInfo);
}
