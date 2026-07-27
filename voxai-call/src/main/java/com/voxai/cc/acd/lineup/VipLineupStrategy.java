package com.voxai.cc.acd.lineup;

import com.voxai.core.constant.Constant;
import com.voxai.core.po.CallInfo;
import com.voxai.core.strategy.LineupStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class VipLineupStrategy implements LineupStrategy {
    private Logger logger = LoggerFactory.getLogger(VipLineupStrategy.class);

    @Override
    public Long calculateLevel(CallInfo callInfo) {
        Long vipLevel = 0L;
        if (callInfo.getProcessData().containsKey(Constant.VIP_LEVEL)) {
            vipLevel = Long.parseLong(callInfo.getProcessData().get(Constant.VIP_LEVEL).toString());
            logger.info("callId:{} lineup of vipLevel:{}", callInfo.getCallId(), vipLevel);
        }
        return vipLevel - callInfo.getQueueStartTime();
    }
}
