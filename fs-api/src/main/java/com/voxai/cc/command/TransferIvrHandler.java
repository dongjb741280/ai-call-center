package com.voxai.cc.command;

import com.voxai.core.po.CallInfo;
import com.voxai.core.po.DeviceInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.voxai.cc.command.base.BaseHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by dongjb on 2025/09/03
 */
@Component
public class TransferIvrHandler extends BaseHandler {

    /**
     * @param callInfo
     * @param deviceInfo
     * @param ivrId
     */
    public void handler(CallInfo callInfo, DeviceInfo deviceInfo, Long ivrId) {
        try {
            cacheService.addCallInfo(callInfo);
            Map<String, Object> params = new HashMap<>();
            params.put("callId", callInfo.getCallId());
            params.put("deviceId", deviceInfo.getDeviceId());
            params.put("ivrId", ivrId);
            ResponseEntity<String> responseEntity = httpClient.getForEntity("http://cc-ivr:7300/cc-ivr/index/start?callId={callId}&deviceId={deviceId}&ivrId={ivrId}", String.class, params);
            logger.info("transfer ivr success {}", callInfo.getCallId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
