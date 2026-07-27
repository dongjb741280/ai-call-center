package com.voxai.cc.websocket.event;

import com.voxai.cc.websocket.event.base.WsBaseEvent;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class WsAnswerEvent extends WsBaseEvent {

    private Long callId;

    private Boolean answer;

    public Long getCallId() {
        return callId;
    }

    public void setCallId(Long callId) {
        this.callId = callId;
    }

    public Boolean getAnswer() {
        return answer;
    }

    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }
}
