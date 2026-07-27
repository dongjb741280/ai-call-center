package com.voxai.cc.fs.handler;

import com.voxai.core.po.CallInfo;
import com.voxai.core.po.DeviceInfo;
import org.springframework.stereotype.Component;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.fs.event.FsRecordStartEvent;
import com.voxai.cc.fs.handler.base.BaseEventHandler;

/**
 * Created by dongjb on 2025/09/03
 * <p>
 * 录音开始
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
        deviceInfo.setRecordTime(event.getTimestamp() / 1000);
        if (callInfo.getRecordTime() == null) {
            callInfo.setRecordTime(deviceInfo.getRecordTime());
        }
    }
}
