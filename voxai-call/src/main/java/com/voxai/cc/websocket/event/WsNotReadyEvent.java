package com.voxai.cc.websocket.event;

import com.voxai.cc.websocket.event.base.WsBaseEvent;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class WsNotReadyEvent extends WsBaseEvent {

    /**
     *  忙碌类型码
     */
    private Integer notReadyCode;


    public Integer getNotReadyCode() {
        return notReadyCode;
    }

    public void setNotReadyCode(Integer notReadyCode) {
        this.notReadyCode = notReadyCode;
    }
}
