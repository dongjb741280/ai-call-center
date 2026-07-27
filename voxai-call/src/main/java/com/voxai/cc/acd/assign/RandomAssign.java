package com.voxai.cc.acd.assign;

import org.apache.commons.lang3.RandomUtils;
import com.voxai.core.po.AgentInfo;
import com.voxai.core.strategy.AgentStrategy;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class RandomAssign implements AgentStrategy {

    @Override
    public Long calculateLevel(AgentInfo agentInfo) {
        return RandomUtils.nextLong();
    }
}
