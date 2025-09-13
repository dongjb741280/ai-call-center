package org.voice9.cc.fs.event;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by dongjb on 2025/09/03
 */
public class FsRingAsrEvent extends FsBridgeEvent {

    @JSONField(name = "RING-CAUSE")
    private String ringCause;

    public String getRingCause() {
        return ringCause;
    }

    public void setRingCause(String ringCause) {
        this.ringCause = ringCause;
    }
}
