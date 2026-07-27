package com.voxai.cc.acd.assign;

import com.voxai.core.po.AgentInfo;
import com.voxai.core.strategy.AgentStrategy;

/**
 * Created by dongjb on 2025/09/03
 *
 * 累计空闲最长时间，不包含当前的空闲时间
 */
public class TotalReadyAssign implements AgentStrategy {

    @Override
    public Long calculateLevel(AgentInfo agentInfo) {
        return agentInfo.getTotalReadyTime();
    }
}
