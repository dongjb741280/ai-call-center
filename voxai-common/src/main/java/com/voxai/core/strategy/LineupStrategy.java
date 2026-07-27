package com.voxai.core.strategy;

import com.voxai.core.po.CallInfo;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public interface LineupStrategy {

    /**
     * 计算电话多媒体进技能组
     *
     * @param callInfo
     * @return
     */
    Long calculateLevel(CallInfo callInfo);
}
