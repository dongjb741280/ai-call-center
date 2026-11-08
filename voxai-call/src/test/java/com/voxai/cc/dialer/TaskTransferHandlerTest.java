package com.voxai.cc.dialer;

import com.voxai.cc.cache.CacheService;
import com.voxai.cc.command.*;
import com.voxai.cc.fs.FsListen;
import com.voxai.core.entity.Agent;
import com.voxai.core.mapper.AgentMapper;
import com.voxai.core.po.*;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskTransferHandlerTest {
    private TaskTransferHandler handler;
    private CacheService cache;
    private FsListen fs;
    private GroupHandler groups;
    private TransferAgentHandler transfer;
    private TransferIvrHandler ivrs;
    private AgentMapper agents;
    private CallInfo call;
    private DeviceInfo customer;
    @BeforeEach void setup() {
        handler=new TaskTransferHandler(); cache=mock(CacheService.class); fs=mock(FsListen.class);
        groups=mock(GroupHandler.class); transfer=mock(TransferAgentHandler.class); ivrs=mock(TransferIvrHandler.class); agents=mock(AgentMapper.class);
        ReflectionTestUtils.setField(handler,"cacheService",cache); ReflectionTestUtils.setField(handler,"fsListen",fs);
        ReflectionTestUtils.setField(handler,"groupHandler",groups); ReflectionTestUtils.setField(handler,"transferAgentHandler",transfer);
        ReflectionTestUtils.setField(handler,"transferIvrHandler",ivrs); ReflectionTestUtils.setField(handler,"agents",agents);
        call=new CallInfo(); call.setCompanyId(7L); call.setCallId(99L); call.setTaskTransferId(22L); call.setMediaHost("fs:8021");
        customer=new DeviceInfo(); customer.setDeviceId("customer"); customer.setAnswerTime(1700000000000L);
    }
    @Test void answeredCustomerEntersItsTenantGroup() {
        call.setTaskTransferType("group"); GroupInfo group=new GroupInfo(); group.setId(22L); group.setCompanyId(7L); group.setStatus(1);
        when(cache.getGroupInfo(22L)).thenReturn(group); handler.route(call,customer);
        assertEquals(Long.valueOf(22),call.getGroupId()); verify(groups).hander(call,group,"customer"); verifyNoInteractions(fs);
    }
    @Test void crossTenantGroupIsNeverRouted() {
        call.setTaskTransferType("group"); GroupInfo group=new GroupInfo(); group.setCompanyId(8L); group.setStatus(1);
        when(cache.getGroupInfo(22L)).thenReturn(group); handler.route(call,customer);
        verifyNoInteractions(groups); verify(fs).hangupCall("fs:8021",99L,"customer");
    }
    @Test void specifiedReadyAgentIsCalledButBusyAgentIsRejected() {
        call.setTaskTransferType("agent"); Agent target=new Agent(); target.setAgentKey("agent"); target.setCompanyId(7L); target.setStatus(1);
        AgentInfo agent=new AgentInfo(); agent.setCompanyId(7L); agent.setStatus(1); agent.setAgentState(AgentState.READY); agent.setLogoutTime(0L);
        when(agents.selectByPrimaryKey(22L)).thenReturn(target); when(cache.getAgentInfo("agent")).thenReturn(agent);
        handler.route(call,customer); verify(transfer).hanlder(call,agent,"customer");
        agent.setCallId(100L); handler.route(call,customer); verifyNoMoreInteractions(transfer); verify(fs).hangupCall("fs:8021",99L,"customer");
    }
    @Test void ivrReceivesAnsweredCustomerAndFailureHangsUp() {
        call.setTaskTransferType("ivr"); handler.route(call,customer);
        assertEquals(customer.getAnswerTime(),call.getAnswerTime()); verify(ivrs).handler(call,customer,22L);
        doThrow(new IllegalStateException("unavailable")).when(ivrs).handler(call,customer,22L);
        handler.route(call,customer); verify(fs).hangupCall("fs:8021",99L,"customer");
    }
}
