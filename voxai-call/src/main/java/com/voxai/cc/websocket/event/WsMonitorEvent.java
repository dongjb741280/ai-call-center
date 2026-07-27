/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.cc.websocket.event;

import com.voxai.cc.websocket.event.base.WsBaseEvent;

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
