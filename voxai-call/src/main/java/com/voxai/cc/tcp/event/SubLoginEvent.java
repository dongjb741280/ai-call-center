package com.voxai.cc.tcp.event;

import com.voxai.cc.tcp.event.base.SubBaseEvent;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class SubLoginEvent extends SubBaseEvent {


    private Integer stationType;

    public Integer getStationType() {
        return stationType;
    }

    public void setStationType(Integer stationType) {
        this.stationType = stationType;
    }
}
