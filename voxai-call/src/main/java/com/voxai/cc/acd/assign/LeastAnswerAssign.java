package com.voxai.cc.acd.assign;

import com.voxai.core.po.AgentInfo;
import com.voxai.core.strategy.AgentStrategy;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class LeastAnswerAssign implements AgentStrategy {

    @Override
    public Long calculateLevel(AgentInfo agentInfo) {
        return -agentInfo.getTotalAnswerTimes();
    }
}
