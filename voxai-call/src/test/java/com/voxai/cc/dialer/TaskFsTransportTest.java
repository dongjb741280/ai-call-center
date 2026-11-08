package com.voxai.cc.dialer;

import com.voxai.cc.fs.FsListen;
import com.voxai.core.entity.RouteGetway;
import com.voxai.core.esl.inbound.Client;
import com.voxai.core.esl.transport.message.EslMessage;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskFsTransportTest {
    private FsListen fs;
    private Client client;
    @BeforeEach void setup() {
        fs=new FsListen(); client=mock(Client.class); when(client.isActivate()).thenReturn(true);
        ReflectionTestUtils.setField(fs,"fsClient",new HashMap<>(Collections.singletonMap("fs:8021",client)));
        ReflectionTestUtils.setField(fs,"codecs","PCMU,PCMA");
    }
    @Test void sendsCustomerUuidCallIdTimeoutAndHardDeadlineInOriginate() {
        RouteGetway route=new RouteGetway(); route.setProfile("external"); route.setMediaHost("sip.example.invalid"); route.setMediaPort(5060);
        when(client.sendBackgroundApiCommand(anyString(),anyString())).thenReturn(CompletableFuture.completedFuture(null));
        fs.originateTask("fs:8021",route,"10086","13800000000",1234567890123456789L,"attempt-uuid",30,3600);
        ArgumentCaptor<String> args=ArgumentCaptor.forClass(String.class); verify(client).sendBackgroundApiCommand(eq("originate"),args.capture());
        String command=args.getValue(); assertTrue(command.contains("origination_uuid=attempt-uuid")); assertTrue(command.contains("callId=1234567890123456789"));
        assertTrue(command.contains("originate_timeout=30")); assertTrue(command.contains("execute_on_answer=sched_hangup +3600 NORMAL_CLEARING"));
        assertTrue(command.contains("sofia/external/13800000000@sip.example.invalid:5060")); assertTrue(command.endsWith("&park()"));
    }
    @Test void disconnectedOrUncertainMediaNeverCountsAsAnAbsentChannel() {
        when(client.isActivate()).thenReturn(false); assertThrows(IllegalStateException.class,() -> fs.taskChannelExists("fs:8021","uuid"));
        when(client.isActivate()).thenReturn(true); EslMessage response=mock(EslMessage.class); when(response.getBodyLines()).thenReturn(Collections.singletonList("-ERR unavailable")); when(client.sendApiCommand(anyString(),anyString())).thenReturn(response);
        assertThrows(IllegalStateException.class,() -> fs.taskChannelExists("fs:8021","uuid"));
    }
    @Test void reconciliationRunsAfterQueuedEventsAndPropagatesFailure() {
        java.util.concurrent.ThreadPoolExecutor executor=new java.util.concurrent.ThreadPoolExecutor(1,1,0,
                java.util.concurrent.TimeUnit.MILLISECONDS,new java.util.concurrent.LinkedBlockingQueue<>());
        try {
            ReflectionTestUtils.setField(fs,"threadNum",1);
            ReflectionTestUtils.setField(fs,"executorMap",new HashMap<>(Collections.singletonMap(0,executor)));
            List<String> order=new ArrayList<>(); executor.submit(() -> order.add("hangup"));
            fs.withTaskEvents(99L,() -> order.add("reconcile")); assertEquals(Arrays.asList("hangup","reconcile"),order);
            assertThrows(IllegalStateException.class,() -> fs.withTaskEvents(99L,() -> { throw new IllegalStateException("database unavailable"); }));
        } finally { executor.shutdownNow(); }
    }
}
