package com.voxai.cc.cdr;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.voxai.core.entity.*;
import com.voxai.core.mapper.*;
import com.voxai.core.po.CallLogPo;
import com.voxai.core.util.CdrTimes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Shared transactional writer for the direct path and every Rabbit consumer. */
@Service
public class CdrPersistenceService {
    @Autowired private CallLogMapper logs;
    @Autowired private CallDeviceMapper devices;
    @Autowired private CallDetailMapper details;
    @Autowired private CdrNotifyMapper notifications;
    @Autowired private CdrMonthTables tables;

    @Transactional
    public int saveDevice(CallDevice device) {
        if (device == null || StringUtils.isBlank(device.getDeviceId())) {
            throw new IllegalArgumentException("A deviceId is required");
        }
        CdrTimes.normalize(device);
        String month = CdrTimes.month(device.getCallId());
        tables.ensure(month);
        device.setId(null); // IDs of the current and monthly copies are independent.
        device.setMonth("");
        devices.upsertCdr(device);
        device.setMonth(month);
        return devices.upsertCdr(device);
    }

    @Transactional
    public int saveDetails(List<CallDetail> list) {
        if (list == null) { return 0; }
        for (CallDetail detail : list) {
            if (detail == null || detail.getDetailIndex() == null || detail.getDetailIndex() <= 0) {
                throw new IllegalArgumentException("A positive detailIndex is required");
            }
            CdrTimes.normalize(detail);
            String month = CdrTimes.month(detail.getCallId());
            tables.ensure(month);
            detail.setId(null);
            detail.setMonth("");
            details.upsertCdr(detail);
            detail.setMonth(month);
            details.upsertCdr(detail);
        }
        return list.size();
    }

    @Transactional
    public int saveLog(CallLog log) {
        if (log == null) { return 0; }
        CdrTimes.normalize(log);
        log.setId(null);
        String month = CdrTimes.month(log.getCallId());
        log.setMonthTime(month);
        // Detail queries use monthTime even while the call is still in progress.
        tables.ensure(month);
        if (log.getEndTime() != null && log.getEndTime() > 0) {
            logs.upsertCdr(log);
        }
        return logs.upsertCurrentCdr(log);
    }

    @Transactional
    public void complete(CdrCompletion completion) {
        if (completion == null) { throw new IllegalArgumentException("A completion is required"); }
        CallLog log = completion.getCallLog();
        if (log == null || log.getEndTime() == null || log.getEndTime() <= 0) {
            throw new IllegalArgumentException("A completed call is required");
        }
        if (completion.getNotifyVersion() != 1 && completion.getNotifyVersion() != 2) {
            throw new IllegalArgumentException("cdr.notify.version must be 1 or 2");
        }
        tables.ensure(CdrTimes.month(log.getCallId()));
        for (CallDevice device : completion.getDevices()) {
            if (!log.getCallId().equals(device.getCallId())) { throw new IllegalArgumentException("Device callId mismatch"); }
            device.setCompanyId(log.getCompanyId());
            saveDevice(device);
        }
        for (CallDetail detail : completion.getDetails()) {
            if (!log.getCallId().equals(detail.getCallId())) { throw new IllegalArgumentException("Detail callId mismatch"); }
        }
        saveDetails(completion.getDetails());
        saveLog(log);
        if (StringUtils.isBlank(completion.getNotifyUrl())) { return; }
        CallLogPo payload = completion.getNotifyVersion() == 2 ? new CdrNotifyV2() : new CallLogPo();
        BeanUtils.copyProperties(log, payload);
        payload.setCallDeviceList(completion.getDevices());
        if (completion.getNotifyVersion() == 2) { payload.setCallDetailList(completion.getDetails()); }
        CdrNotify notify = new CdrNotify();
        notify.setCallId(log.getCallId());
        notify.setCompanyId(log.getCompanyId());
        notify.setCdrNotifyUrl(completion.getNotifyUrl());
        // Preserve v1's set of JSON fields. recordStartTime is v2-only on the wire.
        com.alibaba.fastjson.JSONObject json = JSON.parseObject(JSON.toJSONString(payload, SerializerFeature.WriteMapNullValue));
        if (completion.getNotifyVersion() == 1) {
            json.remove("recordStartTime");
        } else {
            json.put("callId", String.valueOf(log.getCallId()));
            for (String list : new String[]{"callDeviceList", "callDetailList"}) {
                for (Object item : json.getJSONArray(list)) {
                    ((com.alibaba.fastjson.JSONObject) item).put("callId", String.valueOf(log.getCallId()));
                }
            }
        }
        notify.setContent(JSON.toJSONString(json, SerializerFeature.WriteMapNullValue));
        notify.setCts(System.currentTimeMillis());
        notifications.enqueue(notify);
    }
}
