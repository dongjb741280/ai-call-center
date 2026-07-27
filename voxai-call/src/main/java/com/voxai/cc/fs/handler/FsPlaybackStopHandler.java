package com.voxai.cc.fs.handler;

import com.voxai.core.po.CallInfo;
import com.voxai.core.po.DeviceInfo;
import com.voxai.core.po.NextCommand;
import org.springframework.stereotype.Component;
import com.voxai.cc.fs.handler.base.BaseEventHandler;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.fs.event.FsPlaybackStopEvent;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
@HandlerType("PLAYBACK_STOP")
public class FsPlaybackStopHandler extends BaseEventHandler<FsPlaybackStopEvent> {

    @Override
    public void handleEvent(FsPlaybackStopEvent event) {
        CallInfo callInfo = cacheService.getCallInfo(event.getDeviceId());
        if (callInfo == null) {
            return;
        }
        DeviceInfo deviceInfo = callInfo.getDeviceInfoMap().get(event.getDeviceId());
        if (deviceInfo.getEndTime() != null) {
            return;
        }
        NextCommand nextCommand = callInfo.getNextCommands().size() == 0 ? null : callInfo.getNextCommands().get(0);
        if (deviceInfo == null || nextCommand == null) {
            return;
        }
        doNextCommand(callInfo, deviceInfo, nextCommand);
        logger.info("callId:{} playstop, nextType:{}", callInfo.getCallId(), nextCommand.getNextType());
    }

}
