package com.voxai.cc.cdr;

import com.voxai.cc.cache.CacheService;
import com.voxai.cc.fs.event.FsRecordStartEvent;
import com.voxai.cc.fs.handler.FsRecordStartHandler;
import com.voxai.core.entity.CallDetail;
import com.voxai.core.entity.CallDevice;
import com.voxai.core.entity.CallLog;
import com.voxai.core.enums.CallType;
import com.voxai.core.enums.Direction;
import com.voxai.core.po.CallInfo;
import com.voxai.core.po.DeviceInfo;
import com.voxai.core.util.CdrTimes;
import com.voxai.core.util.SnowflakeIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CdrTimeAndAssemblyTest {
    private static final long START = 1_793_500_000_000L;

    @Test void legacyTimePointsConvertButDurationsAndMissingValuesDoNot() {
        CallDevice device = new CallDevice();
        device.setCallTime(START / 1000); device.setEndTime(START / 1000 + 30);
        device.setTalkTime(12000L); device.setRecordTime(9000L);
        CdrTimes.normalize(device);
        assertEquals(START, device.getCallTime());
        assertEquals(START + 30000, device.getEndTime());
        assertNull(device.getAnswerTime());
        assertEquals(12000L, device.getTalkTime());
        assertEquals(9000L, device.getRecordTime());
        assertNull(CdrTimes.epochMillis(null));
        assertEquals(0, CdrTimes.duration(START + 1, START));
        assertEquals(0, CdrTimes.duration(null, START));
    }

    @Test void oldRecordingTimestampIsRepairedOnlyWhenInsideTheCall() {
        CallLog log = new CallLog();
        log.setCallTime(START); log.setEndTime(START + 30000); log.setRecordTime(START + 5000);
        CdrTimes.normalize(log);
        assertEquals(START + 5000, log.getRecordStartTime());
        assertEquals(25000L, log.getRecordTime());
        CdrTimes.normalize(log);
        assertEquals(25000L, log.getRecordTime());
    }

    @Test void recordStartUsesMillisecondsAndPersistsCacheWithoutUsingDurationAsTimestamp() {
        CacheService cache = mock(CacheService.class);
        CallInfo call = call();
        DeviceInfo device = device();
        call.getDeviceInfoMap().put("a", device);
        when(cache.getCallInfo("a")).thenReturn(call);
        FsRecordStartHandler handler = new FsRecordStartHandler();
        ReflectionTestUtils.setField(handler, "cacheService", cache);
        FsRecordStartEvent event = new FsRecordStartEvent();
        event.setDeviceId("a"); event.setTimestamp((START + 5000) * 1000);
        handler.handleEvent(event);
        assertEquals(START + 5000, device.getRecordStartTime());
        assertEquals(0L, device.getRecordTime());
        assertEquals(START + 5000, call.getRecordStartTime());
        verify(cache).addCallInfo(call);
    }

    @Test void finalSnapshotIncludesDeviceDurationsRecordingAndOpenQueueTime() {
        CallInfo call = call();
        DeviceInfo device = device();
        device.setAnswerTime(START + 2000); device.setRecordStartTime(START + 5000);
        device.setRecord("/record.wav"); call.getDeviceInfoMap().put("a", device);
        call.setAnswerTime(START + 2000);
        CallDetail queue = new CallDetail();
        queue.setTransferType(3); queue.setStartTime(START / 1000);
        call.getCallDetails().add(queue);
        CdrCompletion complete = CdrAssembler.complete(call, "http://example.invalid/cdr");
        assertEquals(28000L, complete.getCallLog().getTalkTime());
        assertEquals(25000L, complete.getCallLog().getRecordTime());
        assertEquals(30000L, complete.getCallLog().getWaitTime());
        assertEquals(0, complete.getCallLog().getAnswerFlag());
        assertEquals(25000L, complete.getDevices().get(0).getRecordTime());
        assertEquals(START + 5000, complete.getCallLog().getRecordStartTime());
        assertEquals(START + 30000, queue.getEndTime());
    }

    @Test void unansweredAndOnlyOneSideAnsweredHaveDistinctFlagsAndZeroTalkTime() {
        CallInfo call = call();
        DeviceInfo device = device();
        call.getDeviceInfoMap().put("a", device);
        assertEquals(1, CdrAssembler.complete(call, null).getCallLog().getAnswerFlag());
        device.setAnswerTime(START + 2000);
        assertEquals(2, CdrAssembler.complete(call, null).getCallLog().getAnswerFlag());
        device.setDeviceType(2);
        CallLog row = CdrAssembler.complete(call, null).getCallLog();
        assertEquals(3, row.getAnswerFlag());
        assertEquals(0L, row.getTalkTime());
    }

    private CallInfo call() {
        CallInfo call = new CallInfo();
        call.setCallId((START - SnowflakeIdWorker.WORK_START) << 22);
        call.setCallTime(START); call.setEndTime(START + 30000);
        call.setCallType(CallType.INBOUND_CALL); call.setDirection(Direction.INBOUND);
        return call;
    }
    private DeviceInfo device() {
        DeviceInfo device = new DeviceInfo();
        device.setDeviceId("a"); device.setDeviceType(1);
        device.setCallTime(START); device.setEndTime(START + 30000);
        return device;
    }
}
