package com.voxai.cc.dialer;

import com.voxai.core.entity.*;
import com.voxai.core.mapper.*;
import com.voxai.core.po.dialer.*;
import org.junit.jupiter.api.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskDialerStoreTest {
    private TaskManagementMapper tasks;
    private TaskDialerMapper dialer;
    private TaskMediaService media;
    private TaskDialerStore store;
    private TaskConfig task;
    private TaskAttempt attempt;
    private DialerRequest request;
    @BeforeEach void setup() {
        tasks=mock(TaskManagementMapper.class); dialer=mock(TaskDialerMapper.class); media=mock(TaskMediaService.class); store=new TaskDialerStore(tasks,dialer);
        task=new TaskConfig(); task.setId(11L); task.setCompanyId(7L); task.setStatus(1);
        attempt=new TaskAttempt(); attempt.setAttemptId("attempt"); attempt.setTaskId(11L); attempt.setCompanyId(7L); attempt.setLeaseToken("lease"); attempt.setLeaseUntil(Instant.now().getEpochSecond()+120); attempt.setState("claimed");
        request=new DialerRequest(); request.setAttemptId("attempt"); request.setLeaseToken("lease");
        when(dialer.lockCompany(7L)).thenReturn(7L); when(tasks.lockTask(7L,11L)).thenReturn(task); when(dialer.lockAttempt(7L,11L,"attempt")).thenReturn(attempt);
        DialerCapacity capacity=new DialerCapacity(); capacity.setAllowance(1); when(media.capacity(7L,11L)).thenReturn(capacity);
        TaskContact contact=new TaskContact(); contact.setTaskId(11L); contact.setActiveAttemptId("attempt"); when(tasks.contactByAttempt("attempt")).thenReturn(contact);
        when(media.destination(task,contact)).thenReturn(new TaskMediaService.Destination("fs:8021",new RouteGetway(),"10086"));
        when(media.nextCallId()).thenReturn(99L); when(dialer.assignCall(any())).thenReturn(1);
    }
    @Test void repeatedReservationReturnsExistingCallAndCannotOriginateAgain() {
        TaskDialerStore.Reservation first=store.reserve(7L,11L,request,media); assertTrue(first.fresh); assertEquals(Long.valueOf(99),first.attempt.getCallId());
        TaskDialerStore.Reservation second=store.reserve(7L,11L,request,media); assertFalse(second.fresh);
        verify(dialer,times(1)).assignCall(any()); verify(media,never()).originate(any());
    }
    @Test void expiredOrWrongLeaseCannotReserve() {
        request.setLeaseToken("wrong"); assertThrows(ResponseStatusException.class,() -> store.reserve(7L,11L,request,media));
        request.setLeaseToken("lease"); attempt.setLeaseUntil(Instant.now().getEpochSecond()-1);
        assertThrows(ResponseStatusException.class,() -> store.reserve(7L,11L,request,media)); verify(dialer,never()).assignCall(any());
    }
    @Test void pendingPauseAndTenantMismatchCannotReserve() {
        task.setRequestedStatus(2); assertThrows(ResponseStatusException.class,() -> store.reserve(7L,11L,request,media));
        assertThrows(ResponseStatusException.class,() -> store.reserve(8L,11L,request,media)); verify(dialer,never()).assignCall(any());
    }
    @Test void secondFenceRejectsPauseAfterReservation() {
        TaskDialerStore.Reservation reservation=store.reserve(7L,11L,request,media); task.setRequestedStatus(2);
        assertThrows(ResponseStatusException.class,() -> store.start(reservation,media)); verify(media,never()).originate(any());
    }
    @Test void delayedFirstWorkerCannotOriginateAfterReconciliationDeadline() {
        TaskDialerStore.Reservation reservation=store.reserve(7L,11L,request,media); attempt.setDispatchTime(Instant.now().getEpochSecond()-60);
        assertThrows(ResponseStatusException.class,() -> store.start(reservation,media)); verify(media,never()).originate(any());
    }
    @Test void freshReservationStartsUnderSameLocksUsedByReconciliation() {
        TaskDialerStore.Reservation reservation=store.reserve(7L,11L,request,media); store.start(reservation,media); verify(media).originate(reservation);
    }
}
