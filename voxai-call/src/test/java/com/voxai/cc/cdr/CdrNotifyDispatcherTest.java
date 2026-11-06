package com.voxai.cc.cdr;

import com.voxai.core.entity.CdrNotify;
import com.voxai.core.mapper.CdrNotifyMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class CdrNotifyDispatcherTest {
    private CdrNotifyMapper mapper;
    private CdrNotify row;
    private CdrNotifyDispatcher dispatcher;
    private MockRestServiceServer server;
    @BeforeEach void setUp() {
        mapper = mock(CdrNotifyMapper.class);
        dispatcher = new CdrNotifyDispatcher();
        RestTemplate rest = new RestTemplate();
        server = MockRestServiceServer.bindTo(rest).build();
        ReflectionTestUtils.setField(dispatcher, "notifications", mapper);
        ReflectionTestUtils.setField(dispatcher, "restTemplate", rest);
        ReflectionTestUtils.setField(dispatcher, "maxAttempts", 3);
        row = new CdrNotify(); row.setCallId(123L); row.setPushTimes(0);
        row.setContent("{\"callId\":123}"); row.setCdrNotifyUrl("http://localhost/cdr");
        when(mapper.pending(anyLong())).thenReturn(Collections.singletonList(row));
        when(mapper.claim(row)).thenReturn(1);
    }
    @Test void successfulPostUsesStableIdAndFinishesLease() {
        server.expect(requestTo("http://localhost/cdr")).andExpect(method(HttpMethod.POST))
                .andExpect(header("X-CDR-Id", "123"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(row.getContent())).andRespond(withSuccess("OK", MediaType.TEXT_PLAIN));
        dispatcher.dispatch();
        server.verify(); assertEquals(2, row.getStatus());
        verify(mapper).finish(row);
        assertNotNull(row.getLeaseToken());
    }
    @Test void failedPostSchedulesRetryAndExhaustedPostStopsAutomatically() {
        server.expect(requestTo("http://localhost/cdr")).andRespond(withServerError());
        long before = System.currentTimeMillis();
        dispatcher.dispatch();
        assertEquals(1, row.getStatus()); assertTrue(row.getUts() > before);
        row.setPushTimes(2);
        server.reset();
        server.expect(requestTo("http://localhost/cdr")).andRespond(withStatus(HttpStatus.BAD_REQUEST));
        dispatcher.dispatch(); server.verify();
        assertEquals(3, row.getStatus()); verify(mapper, times(2)).finish(row);
    }
    @Test void workerThatLosesClaimDoesNotSendHttp() {
        when(mapper.claim(row)).thenReturn(0);
        dispatcher.dispatch(); server.verify(); verify(mapper, never()).finish(any());
    }
}
