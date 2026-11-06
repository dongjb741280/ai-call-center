package com.voxai.cc.listen;

import com.voxai.cc.cdr.CdrPersistenceService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CallCdrListenRegressionTest {
    @Test
    void deviceFailureMustReachRabbitContainerInsteadOfBeingAcknowledged() {
        CdrPersistenceService mapper = mock(CdrPersistenceService.class);
        CallCdrListen listener = new CallCdrListen();
        ReflectionTestUtils.setField(listener, "persistence", mapper);
        doThrow(new IllegalStateException("database unavailable")).when(mapper).saveDevice(any());
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> listener.listenCallDevice("{\"callId\":1,\"deviceId\":\"a\"}"));
    }
}
