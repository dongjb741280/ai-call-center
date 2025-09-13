package org.voice9.cc.tcp.event;

import org.voice9.cc.tcp.event.base.SubBaseEvent;

/**
 * Created by dongjb on 2025/09/03
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
