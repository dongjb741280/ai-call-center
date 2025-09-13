package com.voice9.core.strategy;

import com.voice9.core.po.AgentInfo;

/**
 * Created by dongjb on 2025/09/03
 * <p>
 * 坐席在技能组空闲策略
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
