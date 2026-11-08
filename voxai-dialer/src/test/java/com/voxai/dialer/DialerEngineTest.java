package com.voxai.dialer;

import com.voxai.core.entity.TaskConfig;
import com.voxai.core.po.dialer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import java.time.Instant;
import java.util.*;
import static org.mockito.Mockito.*;

class DialerEngineTest {
    private DialerGateway gateway;
    private DialerEngine engine;
    private TaskConfig task;
    @BeforeEach void setup() {
        gateway=mock(DialerGateway.class); DialerProperties properties=new DialerProperties(); properties.setBatchSize(2);
        engine=new DialerEngine(gateway,properties);
        task=new TaskConfig(); task.setId(11L); task.setStatus(1); task.setVersion(3L); task.setCloseTime(Instant.now().getEpochSecond()+600);
        when(gateway.tasks(0)).thenReturn(Collections.singletonList(task));
        when(gateway.active(11L,"")).thenReturn(Collections.emptyList()); when(gateway.pending(11L)).thenReturn(1);
        DialerCapacity capacity=new DialerCapacity(); capacity.setAllowance(2); when(gateway.capacity(11L)).thenReturn(capacity);
    }
    @Test void dispatchesOnlyWithinBatchAndStopsAtEmptyClaim() {
        DialerWork work=work(); work.setCallId(null);
        when(gateway.claim(eq(11L),anyString())).thenReturn(Collections.singletonList(work),Collections.emptyList());
        engine.tick(); verify(gateway,times(2)).claim(eq(11L),anyString()); verify(gateway).dial(work);
    }
    @Test void restartRecoversAndCompletesBeforeClaiming() {
        DialerWork work=work(); when(gateway.active(11L,"")).thenReturn(Collections.singletonList(work));
        DialerStatus done=new DialerStatus(); done.setCompleted(true); done.setCallId(99L); done.setResult("answered"); when(gateway.status(work)).thenReturn(done);
        engine.tick(); InOrder order=inOrder(gateway); order.verify(gateway).heartbeat(work); order.verify(gateway).status(work); order.verify(gateway).complete(work,done); order.verify(gateway).pending(11L);
        verify(gateway,never()).dial(work);
    }
    @Test void pausedAndClosedTasksStillRecoverButNeverClaim() {
        DialerWork work=work(); when(gateway.active(11L,"")).thenReturn(Collections.singletonList(work)); when(gateway.status(work)).thenReturn(new DialerStatus());
        for (int status:new int[]{2,3}) { task.setStatus(status); engine.tick(); }
        verify(gateway,times(2)).heartbeat(work); verify(gateway,never()).claim(anyLong(),anyString());
    }
    @Test void pauseCommandFencesUndispatchedWorkBeforeAcknowledging() {
        DialerWork work=work(); work.setCallId(null); task.setRequestedStatus(2);
        when(gateway.active(11L,"")).thenReturn(Collections.singletonList(work)); engine.tick();
        verify(gateway).acknowledge(task,true); verify(gateway,never()).dial(any()); verify(gateway,never()).claim(anyLong(),anyString());
    }
    @Test void startAcknowledgesOnlyAfterCallControlValidatesTask() {
        task.setStatus(2); task.setRequestedStatus(1); engine.tick();
        InOrder order=inOrder(gateway); order.verify(gateway).capacity(11L); order.verify(gateway).acknowledge(task,true);
        verify(gateway,never()).claim(anyLong(),anyString());
    }
    @Test void connectionFailureLeavesCommandPendingForRetry() {
        task.setStatus(2); task.setRequestedStatus(1); when(gateway.capacity(11L)).thenThrow(new IllegalStateException("offline"));
        engine.tick(); verify(gateway,never()).acknowledge(any(),anyBoolean());
    }
    @Test void expiredTaskOrDrainedContactListRequestsGracefulClose() {
        task.setCloseTime(Instant.now().getEpochSecond()-1); engine.tick(); verify(gateway).close(task); verify(gateway,never()).claim(anyLong(),anyString());
        reset(gateway); when(gateway.tasks(0)).thenReturn(Collections.singletonList(task)); when(gateway.active(11L,"")).thenReturn(Collections.emptyList());
        task.setCloseTime(Instant.now().getEpochSecond()+600); when(gateway.pending(11L)).thenReturn(0); engine.tick(); verify(gateway).close(task);
    }
    @Test void failedResultWriteIsRetriedWithoutAnotherOriginate() {
        DialerWork work=work(); when(gateway.active(11L,"")).thenReturn(Collections.singletonList(work));
        DialerStatus done=new DialerStatus(); done.setCompleted(true); when(gateway.status(work)).thenReturn(done); doThrow(new IllegalStateException()).when(gateway).complete(work,done);
        engine.tick(); engine.tick(); verify(gateway,times(2)).complete(work,done); verify(gateway,never()).dial(work);
    }
    private DialerWork work() {
        DialerWork work=new DialerWork(); work.setTaskId(11L); work.setCallId(99L); work.setAttemptId("attempt_000000001"); work.setLeaseToken("secret"); work.setRequestId("request_000000001"); work.setLeaseUntil(Instant.now().getEpochSecond()+120); return work;
    }
}
