package com.voxai.cc.websocket.event;

import com.voxai.cc.websocket.event.base.WsBaseEvent;

import java.util.Map;

/**
 * Created by dongjb on 2025/09/03
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
