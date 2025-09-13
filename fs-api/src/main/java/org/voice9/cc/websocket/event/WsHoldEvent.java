package org.voice9.cc.websocket.event;

import org.voice9.cc.websocket.event.base.WsBaseEvent;

/**
 * Create by dongjb on 2025/09/03
 * <p>
 * 呼叫保持
 */
public class WsHoldEvent extends WsBaseEvent {

    private Long callId;

    public Long getCallId() {
        return callId;
    }

    public void setCallId(Long callId) {
        this.callId = callId;
    }
}
