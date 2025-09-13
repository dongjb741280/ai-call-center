package org.voice9.cc.fs.event;

import org.voice9.cc.fs.event.base.FsBaseEvent;

/**
 * Created by dongjb on 2025/09/03
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
