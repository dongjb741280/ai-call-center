package com.voxai.cc.dialer;

import com.voxai.cc.cdr.*;
import com.voxai.cc.service.impl.CallCdrServiceImpl;
import com.voxai.core.entity.CallLog;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskCdrCompletionTest {
    @Test void taskCompletionCommitsBeforeClearingContextEvenInPressureMode() {
        CallCdrServiceImpl service=new CallCdrServiceImpl(); CdrPersistenceService persistence=mock(CdrPersistenceService.class);
        CdrPublisher publisher=mock(CdrPublisher.class);
        ReflectionTestUtils.setField(service,"cdrPersistence",persistence); ReflectionTestUtils.setField(service,"cdrPublisher",publisher);
        ReflectionTestUtils.setField(service,"callCdrPressure",1); ReflectionTestUtils.setField(service,"notifyVersion",2);
        CdrCompletion completion=new CdrCompletion(); CallLog log=new CallLog(); log.setTaskId(11L); completion.setCallLog(log);
        service.completeCall(completion); verify(persistence).complete(completion); verifyNoInteractions(publisher); assertEquals(2,completion.getNotifyVersion());
        doThrow(new IllegalStateException("database unavailable")).when(persistence).complete(completion);
        assertThrows(IllegalStateException.class,() -> service.completeCall(completion));
    }
}
