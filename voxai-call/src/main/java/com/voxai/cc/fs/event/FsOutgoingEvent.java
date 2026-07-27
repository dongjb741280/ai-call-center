package com.voxai.cc.fs.event;

import com.voxai.cc.fs.event.base.FsBaseEvent;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class FsOutgoingEvent extends FsBaseEvent {


    @Override
    public String toString() {
        return "OutgoingEvent{" +
                "direction='" + direction + '\'' +
                ", eventName='" + eventName + '\'' +
                ", context=" + context +
                ", coreUuid='" + coreUuid + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", timestamp=" + timestamp +
                ", channelName='" + channelName + '\'' +
                ", map=" + map +
                '}';
    }
}
