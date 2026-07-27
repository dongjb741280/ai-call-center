package com.voxai.cc.websocket.event;

import com.voxai.cc.websocket.event.base.WsBaseEvent;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class WsTransferEvent extends WsBaseEvent {

    private String target;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
