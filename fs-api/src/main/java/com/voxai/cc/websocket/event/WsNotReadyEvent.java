package com.voxai.cc.websocket.event;

import com.voxai.cc.websocket.event.base.WsBaseEvent;

/**
 * Create by dongjb on 2025/09/03
 * <p>
 * 忙碌
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
