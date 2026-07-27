package com.voxai.cc.fs.handler;

import com.voxai.core.po.CallInfo;
import com.voxai.cc.fs.handler.base.BaseEventHandler;
import com.voxai.cc.fs.event.FsPlaybackStartEvent;

/**
 * @author dongjb
 * @date 2026/07/27
 */
//@Component
//@HandlerType("PLAYBACK_START")
public class FsPlaybackStartHandler extends BaseEventHandler<FsPlaybackStartEvent> {
    @Override
    public void handleEvent(FsPlaybackStartEvent event) {
        CallInfo callInfo = cacheService.getCallInfo(event.getDeviceId());
        if (callInfo == null) {
            return;
        }
        logger.info("callId:{} deviceId:{} 放音成功:{}", callInfo.getCallId(), event.getDeviceId(), event.toString());
    }
}
