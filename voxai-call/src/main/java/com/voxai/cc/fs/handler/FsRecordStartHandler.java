package com.voxai.cc.fs.handler;

import com.voxai.core.po.CallInfo;
import com.voxai.core.po.DeviceInfo;
import org.springframework.stereotype.Component;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.fs.event.FsRecordStartEvent;
import com.voxai.cc.fs.handler.base.BaseEventHandler;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
@HandlerType("RECORD_START")
public class FsRecordStartHandler extends BaseEventHandler<FsRecordStartEvent> {

    @Override
    public void handleEvent(FsRecordStartEvent event) {
        CallInfo callInfo = cacheService.getCallInfo(event.getDeviceId());
        if (callInfo == null) {
            return;
        }
        DeviceInfo deviceInfo = callInfo.getDeviceInfoMap().get(event.getDeviceId());
        if (deviceInfo == null) { return; }
        long startedAt = event.getTimestamp() / 1000L; // ESL microseconds -> epoch milliseconds.
        deviceInfo.setRecordStartTime(startedAt);
        deviceInfo.setRecordTime(0L);
        if (callInfo.getRecordStartTime() == null || startedAt < callInfo.getRecordStartTime()) {
            callInfo.setRecordStartTime(startedAt);
        }
        cacheService.addCallInfo(callInfo);
    }
}
