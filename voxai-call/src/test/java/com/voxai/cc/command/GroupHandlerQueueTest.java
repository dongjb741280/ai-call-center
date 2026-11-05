package com.voxai.cc.command;

import com.voxai.core.constant.Constant;
import com.voxai.core.entity.OverflowFront;
import com.voxai.core.po.CallInfo;
import com.voxai.core.po.GroupInfo;
import com.voxai.core.po.GroupOverflowPo;
import com.voxai.cc.acd.lineup.DefaultLineupStrategy;
import com.voxai.cc.acd.lineup.CustomLineupStrategy;
import com.voxai.cc.acd.lineup.VipLineupStrategy;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class GroupHandlerQueueTest {

    @Test
    void queueUsesPriorityThenArrivalTime() {
        GroupHandler.CallQueue earlierLowPriority = queue(1L, 1L, 1000L);
        GroupHandler.CallQueue laterLowPriority = queue(1L, 2L, 2000L);
        GroupHandler.CallQueue vip = queue(2L, 3L, 3000L);
        PriorityQueue<GroupHandler.CallQueue> queue = new PriorityQueue<>();
        queue.add(laterLowPriority);
        queue.add(vip);
        queue.add(earlierLowPriority);

        assertSame(vip, queue.poll());
        assertSame(earlierLowPriority, queue.poll());
        assertSame(laterLowPriority, queue.poll());
    }

    @Test
    void defaultAndVipLineupLevelsDoNotMixWithTimestamp() {
        CallInfo callInfo = new CallInfo();
        callInfo.setQueueStartTime(1_800_000_000_000L);
        Map<String, Object> processData = new HashMap<>();
        processData.put(Constant.VIP_LEVEL, 3);
        callInfo.setProcessData(processData);

        assertEquals(0L, new DefaultLineupStrategy().calculateLevel(callInfo));
        assertEquals(3L, new VipLineupStrategy().calculateLevel(callInfo));
    }

    @Test
    void customLineupUsesConfiguredExpression() {
        CallInfo callInfo = new CallInfo();
        callInfo.setCallTime(10L);
        callInfo.setQueueStartTime(20L);
        assertEquals(30L, new CustomLineupStrategy("#startTime + #queueTime").calculateLevel(callInfo));
    }

    @Test
    void effectiveOverflowFallsThroughToNextBinding() {
        GroupOverflowPo unavailable = overflow(1);
        GroupOverflowPo available = overflow(0);
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setId(10L);
        groupInfo.setGroupOverflows(Arrays.asList(unavailable, available));

        assertSame(available, new GroupHandler().getEffectiveOverflow(groupInfo));
    }

    private GroupHandler.CallQueue queue(Long priority, Long callId, Long startedAt) {
        return new GroupHandler().new CallQueue(priority, callId, "device", startedAt,
                1L, new GroupOverflowPo());
    }

    private GroupOverflowPo overflow(int threshold) {
        OverflowFront front = new OverflowFront();
        front.setFrontType(1);
        front.setCompareCondition(3);
        front.setRankValue(threshold);
        GroupOverflowPo overflow = new GroupOverflowPo();
        overflow.setOverflowFronts(Arrays.asList(front));
        return overflow;
    }
}
