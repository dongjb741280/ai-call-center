package com.voxai.cc.acd.lineup;

import com.voxai.core.po.CallInfo;
import com.voxai.core.strategy.LineupStrategy;

/**
 * Created by dongjb on 2025/09/03
 * <p>
 * 默认按照进技能组时间，时间越小转坐席越早
 */
public class DefaultLineupStrategy implements LineupStrategy {

    @Override
    public Long calculateLevel(CallInfo callInfo) {
        return -callInfo.getQueueStartTime();
    }
}
