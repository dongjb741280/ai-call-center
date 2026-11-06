package com.voxai.cc.cdr;

import com.alibaba.fastjson.JSON;
import com.voxai.core.entity.CallDevice;
import com.voxai.core.entity.CallLog;
import com.voxai.core.po.CallInfo;
import com.voxai.core.po.DeviceInfo;
import com.voxai.core.util.CdrTimes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import java.util.Comparator;
import java.util.Collections;

/** Build the final snapshot before clearing Redis. No database re-read is needed in pressure mode. */
public final class CdrAssembler {
    private CdrAssembler() { }

    public static CdrCompletion complete(CallInfo call, String notifyUrl) {
        CdrCompletion result = new CdrCompletion();
        result.setNotifyUrl(notifyUrl);
        result.setDetails(call.getCallDetails() == null ? new java.util.ArrayList<>() : call.getCallDetails());
        (call.getDeviceInfoMap() == null ? Collections.<DeviceInfo>emptyList() : call.getDeviceInfoMap().values())
                .stream().sorted(Comparator.comparing(DeviceInfo::getDeviceId, Comparator.nullsFirst(String::compareTo))).forEach(device -> {
            CallDevice row = new CallDevice();
            BeanUtils.copyProperties(device, row);
            row.setCompanyId(call.getCompanyId());
            row.setCts(row.getEndTime());
            row.setUts(row.getEndTime());
            CdrTimes.normalize(row);
            row.setTalkTime(CdrTimes.duration(row.getAnswerTime(), row.getEndTime()));
            row.setRecordTime(CdrTimes.duration(row.getRecordStartTime(), row.getEndTime()));
            result.getDevices().add(row);
        });
        CallLog log = new CallLog();
        BeanUtils.copyProperties(call, log);
        CdrTimes.normalize(log);
        if (log.getEndTime() == null || log.getEndTime() <= 0) {
            log.setEndTime(result.getDevices().stream().map(CallDevice::getEndTime)
                    .filter(java.util.Objects::nonNull).max(Long::compareTo).orElse(call.getCallTime()));
        }
        log.setCts(CdrTimes.epochMillis(call.getCallTime()));
        log.setUts(log.getEndTime());
        log.setMonthTime(CdrTimes.month(call.getCallId()));
        log.setCallType(call.getCallType().name());
        log.setDirection(call.getDirection().name());
        log.setTalkTime(CdrTimes.duration(log.getAnswerTime(), log.getEndTime()));
        boolean agentAnswered = result.getDevices().stream().anyMatch(d -> Integer.valueOf(1).equals(d.getDeviceType())
                && d.getAnswerTime() != null && d.getAnswerTime() > 0);
        boolean customerAnswered = result.getDevices().stream().anyMatch(d -> !Integer.valueOf(1).equals(d.getDeviceType())
                && d.getAnswerTime() != null && d.getAnswerTime() > 0);
        log.setAnswerFlag(log.getAnswerTime() != null && log.getAnswerTime() > 0 ? 0
                : agentAnswered ? 2 : customerAnswered ? 3 : 1);
        result.getDetails().forEach(detail -> {
            CdrTimes.normalize(detail);
            if (detail.getEndTime() == null || detail.getEndTime() <= 0) {
                detail.setEndTime(log.getEndTime());
            }
        });
        log.setRecordTime(CdrTimes.duration(log.getRecordStartTime(), log.getEndTime()));
        log.setWaitTime(result.getDetails().stream().filter(d -> Integer.valueOf(3).equals(d.getTransferType()))
                .mapToLong(d -> CdrTimes.duration(d.getStartTime(), d.getEndTime())).sum());
        result.getDevices().stream().filter(d -> StringUtils.isNotBlank(d.getRecord()))
                .min(Comparator.comparing(d -> d.getRecordStartTime() == null ? Long.MAX_VALUE : d.getRecordStartTime()))
                .ifPresent(d -> {
                    log.setRecord(d.getRecord());
                    log.setRecordStartTime(d.getRecordStartTime());
                    log.setRecordTime(d.getRecordTime());
                });
        if (call.getFollowData() != null && !call.getFollowData().isEmpty()) {
            log.setFollowData(JSON.toJSONString(call.getFollowData()));
        }
        call.setTalkTime(log.getTalkTime());
        call.setAnswerFlag(log.getAnswerFlag());
        result.setCallLog(log);
        return result;
    }
}
