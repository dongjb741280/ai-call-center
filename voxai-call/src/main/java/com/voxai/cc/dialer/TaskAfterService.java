package com.voxai.cc.dialer;

import com.voxai.cc.cache.CacheService;
import com.voxai.cc.command.GroupHandler;
import com.voxai.core.po.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

/** The deadline lives in Redis with the agent so a call-service restart does not lose after-call time. */
@Service
public class TaskAfterService {
    private final CacheService cache;
    private final GroupHandler groups;
    private final DialerControlProperties properties;
    private final com.voxai.cc.service.AgentService agents;
    private final com.voxai.cc.websocket.WebSocketHandler websocket;
    public TaskAfterService(CacheService cache,GroupHandler groups,DialerControlProperties properties,com.voxai.cc.service.AgentService agents,com.voxai.cc.websocket.WebSocketHandler websocket) {
        this.cache=cache; this.groups=groups; this.properties=properties; this.agents=agents; this.websocket=websocket;
    }
    private void publish(AgentInfo agent) {
        agents.syncAgentStateMessage(agent);
        if (agent.getHost()!=null && !agent.getHost().isEmpty()) {
            websocket.sendMessgae(agent,com.alibaba.fastjson.JSON.toJSONString(new com.voxai.cc.websocket.response.WsResponseEntity<>(agent.getAgentState().name(),agent.getAgentKey())));
        }
    }
    public void begin(CallInfo call,AgentInfo agent) {
        long now=Instant.now().getEpochSecond();
        agent.setBeforeState(agent.getAgentState()); agent.setBeforeTime(agent.getStateTime());
        agent.setCallId(null); agent.setDeviceId(null); agent.setAgentState(AgentState.AFTER); agent.setStateTime(now);
        agent.setTaskAfterUntil(now+(call.getTaskAfterDuration()==null?0:Math.max(0,call.getTaskAfterDuration())));
        cache.addAgentInfo(agent);
        if (agent.getGroupIds()!=null) { groups.agentNotReady(agent); }
        publish(agent);
    }
    @Scheduled(fixedDelayString="${dialer.control.after-poll-millis:1000}")
    public void tick() {
        for (Long company:properties.getTokens().keySet()) {
            CompanyInfo owner=cache.getCompany(company);
            if (owner==null || owner.getGroupIds()==null) { continue; }
            Set<String> visited=new HashSet<>();
            for (Long groupId:owner.getGroupIds()) {
                GroupInfo group=cache.getGroupInfo(groupId);
                if (group==null || group.getOnlineAgents()==null) { continue; }
                for (String key:new ArrayList<>(group.getOnlineAgents())) {
                    if (!visited.add(key)) { continue; }
                    AgentInfo agent=cache.getAgentInfo(key);
                    if (agent==null || !Objects.equals(company,agent.getCompanyId()) || agent.getTaskAfterUntil()==null || agent.getTaskAfterUntil()>Instant.now().getEpochSecond()) { continue; }
                    agent.setTaskAfterUntil(null);
                    if (agent.getAgentState()==AgentState.AFTER && agent.getCallId()==null && Long.valueOf(0).equals(agent.getLogoutTime()) && Integer.valueOf(1).equals(agent.getStatus())) {
                        agent.setBeforeState(AgentState.AFTER); agent.setBeforeTime(agent.getStateTime());
                        agent.setStateTime(Instant.now().getEpochSecond()); agent.setAgentState(AgentState.READY);
                        cache.addAgentInfo(agent);
                        if (agent.getHost()==null || agent.getHost().isEmpty()) { groups.agentFree(agent); }
                        publish(agent);
                    } else { cache.addAgentInfo(agent); }
                }
            }
        }
    }
}
