package com.voxai.cc.acd.assign;

import com.voxai.core.po.AgentInfo;
import com.voxai.core.strategy.AgentStrategy;

/**
 * Created by dongjb on 2025/09/03
 * <p>
 * 坐席轮选
 */
public class PollAssign implements AgentStrategy {
    @Override
    public Long calculateLevel(AgentInfo agentInfo) {
        return -agentInfo.getServiceTime();
    }
}
