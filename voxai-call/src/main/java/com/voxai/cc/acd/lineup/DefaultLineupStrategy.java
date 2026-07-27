package com.voxai.cc.acd.lineup;

import com.voxai.core.po.CallInfo;
import com.voxai.core.strategy.LineupStrategy;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class DefaultLineupStrategy implements LineupStrategy {

    @Override
    public Long calculateLevel(CallInfo callInfo) {
        return -callInfo.getQueueStartTime();
    }
}
