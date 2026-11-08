package com.voxai.cc.dialer;

import com.voxai.cc.command.base.BaseHandler;
import com.voxai.core.entity.Agent;
import com.voxai.core.mapper.AgentMapper;
import com.voxai.core.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Objects;

/** Routes an answered customer leg; task target IDs never come from an untrusted event header. */
@Component
public class TaskTransferHandler extends BaseHandler {
    @Autowired private AgentMapper agents;
    public void route(CallInfo call,DeviceInfo customer) {
        try { routeTarget(call,customer); }
        catch (RuntimeException failure) { hangup(call,customer); }
    }
    private void routeTarget(CallInfo call,DeviceInfo customer) {
        if ("group".equals(call.getTaskTransferType())) {
            GroupInfo group=cacheService.getGroupInfo(call.getTaskTransferId());
            if (group==null || !Objects.equals(call.getCompanyId(),group.getCompanyId()) || !Integer.valueOf(1).equals(group.getStatus())) { hangup(call,customer); return; }
            call.setGroupId(group.getId()); cacheService.addCallInfo(call);
            groupHandler.hander(call,group,customer.getDeviceId());
        } else if ("ivr".equals(call.getTaskTransferType())) {
            call.setAnswerTime(customer.getAnswerTime()); call.setIvrId(call.getTaskTransferId()); cacheService.addCallInfo(call);
            transferIvrHandler.handler(call,customer,call.getTaskTransferId());
        } else if ("agent".equals(call.getTaskTransferType())) {
            Agent target=agents.selectByPrimaryKey(call.getTaskTransferId());
            AgentInfo agent=target==null?null:cacheService.getAgentInfo(target.getAgentKey());
            if (target==null || !Objects.equals(call.getCompanyId(),target.getCompanyId()) || !Integer.valueOf(1).equals(target.getStatus())
                    || agent==null || !Objects.equals(call.getCompanyId(),agent.getCompanyId()) || !Integer.valueOf(1).equals(agent.getStatus())
                    || agent.getAgentState()!=AgentState.READY || agent.getCallId()!=null || !Long.valueOf(0).equals(agent.getLogoutTime())) { hangup(call,customer); return; }
            call.setGroupId(agent.getGroupId()); cacheService.addCallInfo(call);
            transferAgentHandler.hanlder(call,agent,customer.getDeviceId());
        } else { hangup(call,customer); }
    }
    private void hangup(CallInfo call,DeviceInfo device) { fsListen.hangupCall(call.getMediaHost(),call.getCallId(),device.getDeviceId()); }
}
