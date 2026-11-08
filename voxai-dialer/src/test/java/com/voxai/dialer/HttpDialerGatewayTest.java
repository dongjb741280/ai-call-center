package com.voxai.dialer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxai.core.po.dialer.DialerWork;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class HttpDialerGatewayTest {
    private HttpDialerGateway gateway;
    private MockRestServiceServer server;
    @BeforeEach void setup() {
        DialerProperties properties=new DialerProperties(); properties.setCompanyId(7L); properties.setUsername("engine"); properties.setPassword("secret"); properties.setCallToken("01234567890123456789012345678901");
        gateway=new HttpDialerGateway(properties,new ObjectMapper()); server=MockRestServiceServer.bindTo(gateway.http()).build();
    }
    @Test void keepsSnowflakePrecisionAndSeparatesCredentialsByService() {
        server.expect(requestTo("http://127.0.0.1:7100/voxai-admin/config/task/engine?afterId=0&limit=100"))
                .andExpect(header("Authorization","Basic ZW5naW5lOnNlY3JldA=="))
                .andRespond(withSuccess("{\"code\":0,\"data\":[{\"id\":1234567890123456789,\"companyId\":7}]}",MediaType.APPLICATION_JSON));
        DialerWork work=new DialerWork(); work.setTaskId(11L); work.setAttemptId("attempt"); work.setLeaseToken("lease");
        server.expect(requestTo("http://127.0.0.1:7200/voxai-call/internal/dialer/task/11/status"))
                .andExpect(header("Authorization","Bearer 01234567890123456789012345678901")).andExpect(header("X-Company-Id","7"))
                .andRespond(withSuccess("{\"completed\":true,\"callId\":\"1234567890123456789\",\"result\":\"answered\"}",MediaType.APPLICATION_JSON));
        assertEquals(Long.valueOf(1234567890123456789L),gateway.tasks(0).get(0).getId());
        assertEquals(Long.valueOf(1234567890123456789L),gateway.status(work).getCallId()); server.verify();
    }
    @Test void rejectsBusinessFailureAndWrongTenant() {
        server.expect(anything()).andRespond(withSuccess("{\"code\":401,\"data\":[]}",MediaType.APPLICATION_JSON));
        server.expect(anything()).andRespond(withSuccess("{\"code\":0,\"data\":[{\"id\":1,\"companyId\":8}]}",MediaType.APPLICATION_JSON));
        assertThrows(IllegalStateException.class,() -> gateway.tasks(0));
        assertThrows(IllegalStateException.class,() -> gateway.tasks(0)); server.verify();
    }
    @Test void refusesRedirectResponses() {
        server.expect(anything()).andRespond(withStatus(HttpStatus.FOUND));
        assertThrows(IllegalStateException.class,() -> gateway.tasks(0)); server.verify();
    }
    @Test void readsTheAdminClaimContractIncludingItsOptionalContactName() {
        server.expect(requestTo("http://127.0.0.1:7100/voxai-admin/config/task/11/claim"))
                .andRespond(withSuccess("{\"code\":0,\"data\":[{\"taskId\":11,\"contactId\":12,\"attemptId\":\"attempt\",\"requestId\":\"request\",\"leaseToken\":\"lease\",\"leaseUntil\":1700000120,\"round\":1,\"name\":\"contact\",\"phone\":\"13800000000\",\"customData\":null}]}",MediaType.APPLICATION_JSON));
        DialerWork work=gateway.claim(11L,"request").get(0); assertEquals("lease",work.getLeaseToken()); assertEquals(Long.valueOf(12),work.getContactId()); server.verify();
    }
}
