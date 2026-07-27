/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.StatHourAgent;
import com.voxai.core.mapper.base.BaseMapper;

import java.util.List;

public interface StatHourAgentMapper extends BaseMapper<StatHourAgent> {

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    int batchInsert(List<StatHourAgent> list);

    /**
     * 重复统计时，需要先删除
     *
     * @param statTime
     */
    void deleteAgentHourStat(Long statTime);
}