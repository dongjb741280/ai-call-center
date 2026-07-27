package com.voxai.cc.websocket.handler;

import org.apache.commons.lang3.StringUtils;
import com.voxai.core.entity.Agent;
import com.voxai.core.po.AgentInfo;
import com.voxai.core.po.AgentState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.websocket.handler.base.WsBaseHandler;
import com.voxai.cc.websocket.WebSocketHandler;
import com.voxai.cc.websocket.event.WsLogoutEvent;

import java.time.Instant;

/**
 * Created by dongjb on 2025/09/03
 * <p>
 * 坐席退出
 */
@Component
@HandlerType("WS_LOGOUT")
public class WsLogoutHandler extends WsBaseHandler<WsLogoutEvent> {

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Override
    public void handleEvent(WsLogoutEvent event) {
        if (StringUtils.isBlank(event.getAgentKey())) {
            return;
        }
        webSocketHandler.removeAgentChannel(event.getAgentKey());
        if (event.getChannel() != null) {
            if (event.getChannel().isOpen()) {
                event.getChannel().close();
            } else {
                return;
            }
        }
        logger.info("agent {} logout", event.getAgentKey());


        AgentInfo agentInfo = getAgent(event);
        if (agentInfo == null || agentInfo.getAgentState() == AgentState.LOGOUT) {
            return;
        }
        agentInfo.setBeforeState(agentInfo.getAgentState());
        agentInfo.setBeforeTime(agentInfo.getStateTime());
        agentInfo.setStateTime(Instant.now().getEpochSecond());
        agentInfo.setLogoutTime(agentInfo.getStateTime());
        agentInfo.setLoginTime(0L);
        agentInfo.setAgentState(AgentState.LOGOUT);
        agentInfo.setDeviceId(null);
        agentInfo.setCallId(null);

        /**
         *  广播坐席状态
         */
        syncAgentStateMessage(agentInfo);

        /**
         * 坐席下线
         */
        Agent agent = new Agent();
        agent.setId(agentInfo.getId());
        agent.setCompanyId(agentInfo.getCompanyId());
        agent.setState(0);
        agent.setHost(StringUtils.SPACE);
        agentService.editById(agent);
    }
}
