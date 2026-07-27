package com.voxai.api.service;

import com.voxai.core.entity.AgentStateLog;
import com.voxai.core.entity.StatHourAgent;

import java.util.List;
import java.util.Map;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public interface StatWorkService extends BaseService<StatHourAgent> {

    /**
     * 坐席坐席操作日志
     *
     * @param params
     * @return
     */
    List<AgentStateLog> getListByMap(Map<String, Object> params);

    /**
     * 状态统计
     *
     * @param params
     * @return
     */
    List<StatHourAgent> statHour(Map<String, Object> params);


    /**
     * @param agentWorkList
     * @return
     */
    int saveStateHoutAgent(List<StatHourAgent> agentWorkList);


    /**
     * @param statTime
     */
    void deleteAgentHourStat(Long statTime);
}
