package com.voxai.cc.fs.event;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author dongjb
 * @date 2026/07/27
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
