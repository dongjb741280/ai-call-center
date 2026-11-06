package com.voxai.core.util;

import com.voxai.core.po.CallLogPo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

/** CDR time points are epoch milliseconds; all durations are milliseconds. */
public final class CdrTimes {
    private static final String[] TIME_POINTS = {"cts", "uts", "callTime", "answerTime", "endTime",
            "startTime", "ringStartTime", "ringEndTime", "bridgeTime", "recordStartTime",
            "fristQueueTime", "queueStartTime", "queueEndTime"};

    private CdrTimes() { }

    public static Long epochMillis(Long value) {
        // Only timestamps in the supported epoch range are converted, never durations.
        if (value != null && value >= 1_000_000_000L && value < 100_000_000_000L) {
            return value * 1000L;
        }
        return value;
    }

    public static long duration(Long start, Long end) {
        return start == null || start <= 0 || end == null || end <= start ? 0L : end - start;
    }

    public static String month(Long callId) {
        if (callId == null || callId <= 0) {
            throw new IllegalArgumentException("A positive snowflake callId is required");
        }
        return DateTimeUtil.getMonth(DateTimeUtil.getCallTime(callId));
    }

    /** Read compatibility for old second-based timestamps and misused record_time. */
    public static void normalize(Object value) {
        if (value == null) { return; }
        try {
            Map<String, PropertyDescriptor> properties = new HashMap<>();
            for (PropertyDescriptor property : Introspector.getBeanInfo(value.getClass()).getPropertyDescriptors()) {
                properties.put(property.getName(), property);
            }
            for (String name : TIME_POINTS) {
                PropertyDescriptor property = properties.get(name);
                if (property != null && property.getReadMethod() != null && property.getWriteMethod() != null) {
                    Long time = (Long) property.getReadMethod().invoke(value);
                    property.getWriteMethod().invoke(value, epochMillis(time));
                }
            }
            PropertyDescriptor record = properties.get("recordTime");
            PropertyDescriptor start = properties.get("recordStartTime");
            PropertyDescriptor call = properties.get("callTime");
            PropertyDescriptor end = properties.get("endTime");
            if (record != null && start != null && call != null && end != null
                    && record.getReadMethod() != null && start.getWriteMethod() != null
                    && call.getReadMethod() != null && end.getReadMethod() != null) {
                Long recordTime = (Long) record.getReadMethod().invoke(value);
                Long callTime = (Long) call.getReadMethod().invoke(value);
                Long endTime = (Long) end.getReadMethod().invoke(value);
                Long candidate = epochMillis(recordTime);
                if (candidate != null && callTime != null && candidate >= callTime
                        && candidate > 100_000_000_000L && (endTime == null || endTime == 0 || candidate <= endTime)) {
                    start.getWriteMethod().invoke(value, candidate);
                    record.getWriteMethod().invoke(value, duration(candidate, endTime));
                }
            }
        } catch (ReflectiveOperationException | java.beans.IntrospectionException error) {
            throw new IllegalArgumentException("Cannot normalize CDR timestamps", error);
        }
        if (value instanceof CallLogPo) {
            CallLogPo log = (CallLogPo) value;
            if (log.getCallDeviceList() != null) { log.getCallDeviceList().forEach(CdrTimes::normalize); }
            if (log.getCallDetailList() != null) { log.getCallDetailList().forEach(CdrTimes::normalize); }
        }
    }
}
