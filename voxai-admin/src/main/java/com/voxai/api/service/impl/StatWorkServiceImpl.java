package com.voxai.api.service.impl;

import com.google.common.collect.Lists;
import com.voxai.api.service.StatWorkService;
import com.voxai.core.entity.AgentStateLog;
import com.voxai.core.entity.StatHourAgent;
import com.voxai.core.mapper.AgentStateLogMapper;
import com.voxai.core.mapper.StatHourAgentMapper;
import com.voxai.core.mapper.base.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Service
public class StatWorkServiceImpl extends BaseServiceImpl<StatHourAgent> implements StatWorkService {

    @Autowired
    private StatHourAgentMapper statHourAgentMapper;

    @Autowired
    private AgentStateLogMapper agentStateLogMapper;

    @Override
    BaseMapper<StatHourAgent> baseMapper() {
        return statHourAgentMapper;
    }


    @Override
    public List<AgentStateLog> getListByMap(Map<String, Object> params) {
        return agentStateLogMapper.selectListByMap(params);
    }

    @Override
    public List<StatHourAgent> statHour(Map<String, Object> params) {
        return agentStateLogMapper.statHour(params);
    }

    @Override
    public int saveStateHoutAgent(List<StatHourAgent> agentWorkList) {
        if (CollectionUtils.isEmpty(agentWorkList)) {
            return 0;
        }
        List<List<StatHourAgent>> newList = Lists.partition(agentWorkList, batchInsertCnt);
        Integer cnt = 0;
        for (List<StatHourAgent> list : newList) {
            cnt += statHourAgentMapper.batchInsert(list);
        }
        return cnt;
    }

    @Override
    public void deleteAgentHourStat(Long statTime) {
        statHourAgentMapper.deleteAgentHourStat(statTime);
    }

}
