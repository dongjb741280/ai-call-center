package org.voice9.cc.acd.lineup;

import com.voice9.core.po.CallInfo;
import com.voice9.core.strategy.LineupStrategy;

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
