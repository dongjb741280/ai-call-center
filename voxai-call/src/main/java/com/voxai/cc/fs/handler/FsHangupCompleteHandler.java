package com.voxai.cc.fs.handler;

import com.voxai.core.entity.CallDevice;
import com.voxai.core.po.AgentInfo;
import com.voxai.core.po.AgentState;
import com.voxai.core.po.CallInfo;
import com.voxai.core.po.CompanyInfo;
import com.voxai.core.po.DeviceInfo;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.fs.event.FsHangupCompleteEvent;
import com.voxai.cc.fs.handler.base.BaseEventHandler;

import java.io.ByteArrayInputStream;
import java.util.Date;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
@HandlerType("CHANNEL_HANGUP_COMPLETE")
public class FsHangupCompleteHandler extends BaseEventHandler<FsHangupCompleteEvent> {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MinioClient minioClient;

    @Override
    public void handleEvent(FsHangupCompleteEvent event) {
        CallInfo callInfo = cacheService.getCallInfo(event.getDeviceId());
        if (callInfo == null) {
            return;
        }
        logger.info("callId:{} deviceList:{}", callInfo.getCallId(), callInfo.getDeviceList());

        /**
         * 挂机原因
         */
        DeviceInfo deviceInfo = callInfo.getDeviceInfoMap().get(event.getDeviceId());
        if (deviceInfo == null) {
            logger.warn("device:{} is null, callId:{}", event.getDeviceId(), callInfo.getCallId());
            return;
        }
        callInfo.getDeviceList().remove(event.getDeviceId());
        deviceInfo.setHangupCause(event.getHangupCause());
        deviceInfo.setSipProtocol(event.getSipProtocol());
        deviceInfo.setSipStatus(event.getSipStatus());
        deviceInfo.setChannelName(event.getChannelName());
        deviceInfo.setEndTime(event.getTimestamp() / 1000);
        deviceInfo.setTalkTime(com.voxai.core.util.CdrTimes.duration(deviceInfo.getAnswerTime(), deviceInfo.getEndTime()));
        //计算录音时长（毫秒）
        if (deviceInfo.getRecordStartTime() != null) {
            deviceInfo.setRecordTime(com.voxai.core.util.CdrTimes.duration(deviceInfo.getRecordStartTime(), deviceInfo.getEndTime()));
        }
        logger.info("callId:{} deviceId:{} deviceType:{} display:{} called:{} sipStatus:{} sipProtocol:{} hangupCause:{}", callInfo.getCallId(), deviceInfo.getDeviceId(), deviceInfo.getDeviceType(), deviceInfo.getDisplay(), deviceInfo.getCalled(), event.getSipStatus(), event.getSipProtocol(), event.getHangupCause());
        /**
         * 上传录音
         */
        if (StringUtils.isNotBlank(deviceInfo.getRecord())) {
            String day = DateFormatUtils.format(new Date(), "yyyyMMddHH");
            String[] record = deviceInfo.getRecord().split("/");
            String fileName = "/" + day.substring(0, 6) + "/" + day.substring(0, 8) + "/" + day.substring(8, 10) + "/" + record[record.length - 1];
            String url = "http://" + event.getLocalMediaIp() + ":7430" + deviceInfo.getRecord();
            try {
                ResponseEntity<byte[]> responseEntity = restTemplate.getForEntity(url, byte[].class);
                logger.info("get record file:{}", deviceInfo.getRecord());
                ObjectWriteResponse writeResponse = minioClient.putObject(PutObjectArgs.builder().stream(new ByteArrayInputStream(responseEntity.getBody()), responseEntity.getBody().length, -1).object(fileName).bucket("cc-record").build());
                logger.info("callId:{}, record fileName:{}, minioTag:{}", deviceInfo.getCallId(), fileName, writeResponse.etag());
                deviceInfo.setRecord("/cc-record" + fileName);
            } catch (Exception e) {
                logger.error("url:" + url + e.getMessage(), e);
            }
        }

        CallDevice callDevice = new CallDevice();
        BeanUtils.copyProperties(deviceInfo, callDevice);
        callDevice.setCts(deviceInfo.getEndTime());
        callDevice.setUts(callDevice.getEndTime());
        callDevice.setCompanyId(callInfo.getCompanyId());
        callCdrService.saveCallDevice(callDevice);
        callInfo.getDeviceInfoMap().put(callDevice.getDeviceId(), deviceInfo);

        /**
         * 一般情况下，挂断其他所有设备
         */
        if ((deviceInfo.getCdrType() == null || deviceInfo.getCdrType() <= 3) && callInfo.getEndTime() == null) {
            callInfo.setEndTime(deviceInfo.getEndTime());
            if (!CollectionUtils.isEmpty(callInfo.getDeviceList())) {
                callInfo.getDeviceList().forEach(s -> {
                    super.hangupCall(callInfo.getMediaHost(), callInfo.getCallId(), s);
                });
            }
        }
        cacheService.addCallInfo(callInfo);
        //最后一个设备挂机
        if (callInfo.getDeviceList().isEmpty()) {
            hangup(callInfo);
        }
    }

    /**
     * 全部挂机
     *
     * @param callInfo
     */
    private void hangup(CallInfo callInfo) {
        CompanyInfo companyInfo = cacheService.getCompany(callInfo.getCompanyId());
        com.voxai.cc.cdr.CdrCompletion completion = com.voxai.cc.cdr.CdrAssembler.complete(callInfo,
                companyInfo == null ? null : companyInfo.getNotifyUrl());
        // The complete snapshot is persisted or broker-confirmed before Redis is removed.
        callCdrService.completeCall(completion);

        //清空坐席通话状态
        if (callInfo.getAgentKey() != null) {
            AgentInfo agentInfo = cacheService.getAgentInfo(callInfo.getAgentKey());
            if (agentInfo != null) {
                agentInfo.setCallId(null);
                agentInfo.setDeviceId(null);
                agentInfo.setAgentState(AgentState.NOT_READY);
                cacheService.addAgentInfo(agentInfo);
            }
        }
        //清空电话信息
        cacheService.removeCallInfo(callInfo.getCallId());
    }

}
