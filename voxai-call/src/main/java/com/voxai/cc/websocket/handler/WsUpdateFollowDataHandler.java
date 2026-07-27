package com.voxai.cc.websocket.handler;

import com.voxai.core.po.AgentInfo;
import com.voxai.core.po.CallInfo;
import org.springframework.stereotype.Component;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.websocket.handler.base.WsBaseHandler;
import com.voxai.cc.websocket.event.WsUpdateFollowDataEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
@HandlerType("WS_UPDATE_FOLLOWDATA")
public class WsUpdateFollowDataHandler extends WsBaseHandler<WsUpdateFollowDataEvent> {
    @Override
    public void handleEvent(WsUpdateFollowDataEvent event) {
        AgentInfo agentInfo = getAgent(event);
        CallInfo callInfo = cacheService.getCallInfo(agentInfo.getCallId());
        if (callInfo == null) {
            return;
        }
        Map<String, Object> followData = callInfo.getFollowData();
        if (followData == null) {
            followData = new HashMap<>();
        }
        followData.putAll(event.getFollowData());
    }
}
