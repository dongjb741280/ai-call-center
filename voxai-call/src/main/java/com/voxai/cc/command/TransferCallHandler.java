package com.voxai.cc.command;

import com.voxai.core.entity.CallDetail;
import com.voxai.core.entity.RouteGetway;
import com.voxai.core.enums.NextType;
import com.voxai.core.po.CallInfo;
import com.voxai.core.po.DeviceInfo;
import com.voxai.core.po.NextCommand;
import org.springframework.stereotype.Component;
import com.voxai.cc.command.base.BaseHandler;

import java.time.Instant;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
public class TransferCallHandler extends BaseHandler {


    /**
     * 转外呼
     *
     * @param callInfo
     * @param called
     * @param thisDeviceId
     */
    public void hanlder(CallInfo callInfo, String called, String thisDeviceId) {
        logger.info("callId:{} transfer to {}", callInfo.getCallId(), called);
        /**
         * 转外呼
         */
        callInfo.setCalled(called);

        String deviceId = getDeviceId();
        RouteGetway routeGetway = cacheService.getRouteGetway(callInfo.getCompanyId(), callInfo.getCalled());
        if (routeGetway == null) {
            logger.warn("callId:{} origin error, called:{}", callInfo.getCallId(), callInfo.getCalled());
            fsListen.hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), thisDeviceId);
            return;
        }

        DeviceInfo device = DeviceInfo.DeviceInfoBuilder.builder().withDeviceId(deviceId).withDeviceType(3).withCdrType(2).withCaller(callInfo.getCaller()).withCalled(callInfo.getCalled()).withCallId(callInfo.getCallId()).withCallTime(Instant.now().toEpochMilli()).withDisplay(callInfo.getCaller()).build();
        callInfo.getDeviceList().add(deviceId);
        callInfo.getDeviceInfoMap().put(deviceId, device);

        CallDetail transferCall = new CallDetail();
        transferCall.setCallId(callInfo.getCallId());
        transferCall.setStartTime(Instant.now().toEpochMilli());
        transferCall.setDetailIndex(callInfo.getCallDetails().size() + 1);
        transferCall.setTransferType(5);
        callInfo.getCallDetails().add(transferCall);
        callInfo.getNextCommands().add(new NextCommand(thisDeviceId, NextType.NEXT_CALL_BRIDGE, deviceId));
        fsListen.makeCall(routeGetway, callInfo.getCaller(), callInfo.getCalled(), callInfo.getCallId(), deviceId,null, null);

    }
}
