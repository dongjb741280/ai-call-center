/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.AgentStateLog;
import com.voxai.core.entity.StatHourAgent;
import com.voxai.core.mapper.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AgentStateLogMapper extends BaseMapper<AgentStateLog> {


    /**
     * 迁移表
     *
     * @param month
     */
    void createNewTable(@Param("month") String month);


    /**
     * 删除数据
     *
     * @param end
     */
    int clearTable(@Param("end") Long end);

    /**
     * 坐席状态统计
     *
     * @param params
     * @return
     */
    List<StatHourAgent> statHour(Map<String, Object> params);

    List<com.voxai.core.po.AgentMonitorVo> selectAgentMonitor(Map<String, Object> params);
}