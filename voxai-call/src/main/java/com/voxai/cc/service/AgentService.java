package com.voxai.cc.service;

import com.voxai.core.entity.Agent;
import com.voxai.core.po.AgentInfo;

import java.util.List;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public interface AgentService extends BaseService<Agent> {

    /**
     * @param agentKey
     * @return
     */
    AgentInfo getAgentInfo(String agentKey);

    /**
     * @param agentId
     * @return
     */
    List<Long> getAgentGroups(Long agentId);

    /**
     * @param agentId
     * @return
     */
    List<String> getAgentSips(Long agentId);


    /**
     * @param agentInfo
     */
    void syncAgentStateMessage(AgentInfo agentInfo);

    /**
     *
     * @param sip
     * @return
     */
    Agent getAgentBySip(String sip);

    void saveAgentLog(AgentInfo agentInfo);

    /**
     *
     * @return
     */
    String getTokenKey();

}
