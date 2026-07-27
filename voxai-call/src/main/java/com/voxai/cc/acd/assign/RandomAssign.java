package com.voxai.cc.acd.assign;

import org.apache.commons.lang3.RandomUtils;
import com.voxai.core.po.AgentInfo;
import com.voxai.core.strategy.AgentStrategy;

/**
 * Created by dongjb on 2025/09/03
 * <p>
 * 随机分配
 */
public class RandomAssign implements AgentStrategy {

    @Override
    public Long calculateLevel(AgentInfo agentInfo) {
        return RandomUtils.nextLong();
    }
}
