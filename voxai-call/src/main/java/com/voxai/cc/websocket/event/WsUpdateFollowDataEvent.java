package com.voxai.cc.websocket.event;

import com.voxai.cc.websocket.event.base.WsBaseEvent;

import java.util.Map;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class WsUpdateFollowDataEvent extends WsBaseEvent {

    /**
     * 随路数据
     */
    private Map<String, Object> followData;

    public Map<String, Object> getFollowData() {
        return followData;
    }

    public void setFollowData(Map<String, Object> followData) {
        this.followData = followData;
    }
}
