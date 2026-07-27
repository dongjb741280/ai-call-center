package com.voxai.cc.tcp.handler.base;

import com.voxai.core.po.AgentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.voxai.cc.tcp.event.base.SubBaseEvent;
import com.voxai.cc.configration.Handler;
import com.voxai.cc.websocket.WebSocketHandler;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public abstract class SubBaseHandler<T extends SubBaseEvent> implements Handler<T> {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected WebSocketHandler webSocketHandler;

    protected void sendAgentMessage(AgentInfo agentInfo, String payload) {
        webSocketHandler.sendMessgae(agentInfo,payload);
    }
}
