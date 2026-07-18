package org.voice9.cc.websocket.event;

import org.voice9.cc.websocket.event.base.WsBaseEvent;

/**
 * Create by dongjb on 2025/09/03
 * <p>
 * 转接事件
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
