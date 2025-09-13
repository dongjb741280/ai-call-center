package org.voice9.cc.acd.assign;

import com.voice9.core.po.AgentInfo;
import com.voice9.core.strategy.AgentStrategy;

/**
 * Created by dongjb on 2025/09/03
 * <p>
 * 当前最长空闲时间
 */
public class LongReadyAssign implements AgentStrategy {

    @Override
    public Long calculateLevel(AgentInfo agentInfo) {
        return -agentInfo.getStateTime();
    }
}
