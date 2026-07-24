package org.voice9.cc.websocket.event;

import org.voice9.cc.websocket.event.base.WsBaseEvent;

/**
 * 班长监控事件（监听 / 强插 / 耳语）
 */
public class WsMonitorEvent extends WsBaseEvent {

    private String target;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
