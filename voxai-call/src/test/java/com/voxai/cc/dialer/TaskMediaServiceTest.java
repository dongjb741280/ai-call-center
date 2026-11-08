package com.voxai.cc.dialer;

import com.voxai.cc.cache.CacheService;
import com.voxai.cc.cdr.*;
import com.voxai.cc.command.GroupHandler;
import com.voxai.cc.fs.FsListen;
import com.voxai.core.entity.*;
import com.voxai.core.mapper.*;
import com.voxai.core.po.*;
import com.voxai.core.po.dialer.*;
import com.voxai.core.util.SnowflakeIdWorker;
import org.junit.jupiter.api.*;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskMediaServiceTest {
    private TaskDialerStore store;
    private TaskDialerMapper dialer;
    private CacheService cache;
    private FsListen fs;
    private CdrPersistenceService cdr;
    private TaskMediaService service;
    private TaskConfig task;
    private TaskAttempt attempt;
    private DialerRequest request;
    @BeforeEach void setup() {
        store=mock(TaskDialerStore.class); dialer=mock(TaskDialerMapper.class); cache=mock(CacheService.class); fs=mock(FsListen.class); cdr=mock(CdrPersistenceService.class);
        service=new TaskMediaService(store,dialer,cache,fs,mock(GroupHandler.class),mock(AgentMapper.class),mock(IvrWorkflowMapper.class),mock(TaskSourceMapper.class),cdr,new SnowflakeIdWorker(1,1),new DialerControlProperties(),mock(TaskAfterService.class));
        task=new TaskConfig(); task.setId(11L); task.setCompanyId(7L); task.setTransferType("group"); task.setCallTimeout(30);
        attempt=new TaskAttempt(); attempt.setCallId(99L); attempt.setMediaHost("fs:8021"); attempt.setAttemptId("attempt"); attempt.setDispatchTime(Instant.now().getEpochSecond()-100);
        request=new DialerRequest(); when(store.task(7L,11L)).thenReturn(task); when(store.attempt(7L,11L,request)).thenReturn(attempt);
    }
    @Test void mapsCompletedCustomerAnswerToSecondsAndPreservesSnowflakeId() {
        CallLog log=new CallLog(); log.setEndTime(1700000030000L); log.setAnswerTime(1700000005000L); log.setAnswerFlag(0); log.setAgentKey("agent@tenant"); when(dialer.call(7L,11L,99L)).thenReturn(log);
        CallDevice customer=new CallDevice(); customer.setAnswerTime(1700000002000L); when(dialer.customer(7L,99L)).thenReturn(customer);
        DialerStatus result=service.status(7L,11L,request); assertTrue(result.isCompleted()); assertEquals("answered",result.getResult());
        assertEquals(Long.valueOf(1700000002),result.getAnswerTime()); assertEquals(Long.valueOf(1700000005),result.getBridgeTime()); assertEquals(Long.valueOf(99),result.getCallId());
    }
    @Test void unfinishedCallReconcilesThroughItsMediaEventExecutor() {
        doAnswer(invocation -> { invocation.getArgument(1,Runnable.class).run(); return null; }).when(fs).withTaskEvents(eq(99L),any(Runnable.class));
        assertFalse(service.status(7L,11L,request).isCompleted());
        verify(fs).withTaskEvents(eq(99L),any(Runnable.class)); verify(store).reconcile(7L,11L,request,service);
    }
    @Test void mediaDisconnectDoesNotGenerateFailureOrReleaseLiveCall() {
        when(fs.taskChannels("fs:8021",99L)).thenThrow(new IllegalStateException("offline"));
        assertThrows(IllegalStateException.class,() -> service.reconcile(task,attempt)); verifyNoInteractions(cdr); verify(cache,never()).removeCallInfo(anyLong());
    }
    @Test void orphanActiveChannelIsKilledAndWaitsForConfirmedAbsenceBeforeCompletion() {
        when(fs.taskChannels("fs:8021",99L)).thenReturn(new java.util.ArrayList<>(java.util.Collections.singletonList("attempt")));
        when(fs.taskChannelExists("fs:8021","attempt")).thenReturn(true);
        service.reconcile(task,attempt); verify(fs).hangupCall("fs:8021",99L,"attempt"); verifyNoInteractions(cdr);
    }
    @Test void definiteMediaAbsenceFinalizesOnceInsteadOfOriginatingAgain() {
        when(fs.taskChannels("fs:8021",99L)).thenReturn(new java.util.ArrayList<>()); service.reconcile(task,attempt);
        verify(cdr).complete(any(CdrCompletion.class)); verify(cache).removeCallInfo(99L);
        verify(fs,never()).originateTask(anyString(),any(),anyString(),anyString(),anyLong(),anyString(),anyInt(),anyInt());
    }
    @Test void recoveredContextRetainsConfiguredNotificationVersionAndTenantUrl() {
        org.springframework.test.util.ReflectionTestUtils.setField(service,"notifyVersion",2);
        CompanyInfo owner=new CompanyInfo(); owner.setNotifyUrl("https://example.com/cdr"); when(cache.getCompany(7L)).thenReturn(owner);
        when(fs.taskChannels("fs:8021",99L)).thenReturn(new java.util.ArrayList<>()); service.reconcile(task,attempt);
        org.mockito.ArgumentCaptor<CdrCompletion> captured=org.mockito.ArgumentCaptor.forClass(CdrCompletion.class);
        verify(cdr).complete(captured.capture()); assertEquals(2,captured.getValue().getNotifyVersion());
        assertEquals("https://example.com/cdr",captured.getValue().getNotifyUrl());
    }
    @Test void actualDispatchBuildsOnlyTheCustomerLegAndRetainsTheTaskTarget() {
        task.setTransferType("group"); task.setTransferId(22L); task.setAfterCallDuration(30);
        TaskContact contact=new TaskContact(); contact.setPhone("13800000000"); contact.setCustomData("{\"crm\":\"value\"}");
        CompanyInfo owner=new CompanyInfo(); when(cache.getCompany(7L)).thenReturn(owner);
        when(fs.originateTask(anyString(),any(),anyString(),anyString(),anyLong(),anyString(),anyInt(),anyInt()))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(null));
        service.originate(new TaskDialerStore.Reservation(task,attempt,true,contact,new RouteGetway(),"10086"));
        org.mockito.ArgumentCaptor<CallInfo> captured=org.mockito.ArgumentCaptor.forClass(CallInfo.class); verify(cache).addCallInfo(captured.capture());
        CallInfo call=captured.getValue(); assertEquals(Long.valueOf(11),call.getTaskId()); assertEquals(Long.valueOf(22),call.getTaskTransferId());
        assertEquals(1,call.getDeviceList().size()); assertEquals(Integer.valueOf(2),call.getDeviceInfoMap().get("attempt").getDeviceType());
        assertEquals(com.voxai.core.enums.NextType.NEXT_TASK,call.getNextCommands().get(0).getNextType());
        assertEquals("value",call.getFollowData().get("crm"));
    }
    @Test void noAnswerBusyAndInvalidResultsAreExplicit() {
        assertEquals("busy",TaskMediaService.result("USER_BUSY")); assertEquals("no_answer",TaskMediaService.result("NO_ANSWER"));
        assertEquals("invalid",TaskMediaService.result("UNALLOCATED_NUMBER")); assertEquals("failed",TaskMediaService.result("UNKNOWN"));
    }
}
