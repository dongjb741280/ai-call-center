package com.voxai.cc.dialer;

import com.alibaba.fastjson.JSON;
import com.voxai.cc.cache.CacheService;
import com.voxai.cc.cdr.CdrAssembler;
import com.voxai.cc.cdr.CdrPersistenceService;
import com.voxai.cc.command.GroupHandler;
import com.voxai.cc.fs.FsListen;
import com.voxai.core.entity.*;
import com.voxai.core.enums.*;
import com.voxai.core.mapper.*;
import com.voxai.core.po.*;
import com.voxai.core.po.dialer.*;
import com.voxai.core.util.CdrTimes;
import com.voxai.core.util.SnowflakeIdWorker;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;
import static com.voxai.cc.dialer.TaskDialerStore.check;

/** Customer-first calls use the existing event handlers and CDR writer. No originate is retried after reservation. */
@Service
public class TaskMediaService {
    private final TaskDialerStore store;
    private final TaskDialerMapper dialer;
    private final CacheService cache;
    private final FsListen fs;
    private final GroupHandler groups;
    private final AgentMapper agents;
    private final IvrWorkflowMapper ivrs;
    private final TaskSourceMapper sources;
    private final CdrPersistenceService cdr;
    private final SnowflakeIdWorker ids;
    private final DialerControlProperties properties;
    private final TaskAfterService after;
    @org.springframework.beans.factory.annotation.Value("${cdr.notify.version:1}")
    private int notifyVersion = 1;
    public TaskMediaService(TaskDialerStore store, TaskDialerMapper dialer, CacheService cache, FsListen fs,
            GroupHandler groups, AgentMapper agents, IvrWorkflowMapper ivrs, TaskSourceMapper sources,
            CdrPersistenceService cdr, SnowflakeIdWorker ids, DialerControlProperties properties, TaskAfterService after) {
        this.store=store; this.dialer=dialer; this.cache=cache; this.fs=fs; this.groups=groups; this.agents=agents;
        this.ivrs=ivrs; this.sources=sources; this.cdr=cdr; this.ids=ids; this.properties=properties; this.after=after;
    }
    long nextCallId() { return ids.nextId(); }
    void checkWindow(TaskConfig task, long now) {
        check(task.getCloseTime() != null && task.getCloseTime() > now, "Task expired");
        LocalTime time = LocalTime.now();
        check(task.getCallStartTime() != null && task.getCallEndTime() != null &&
                !time.isBefore(LocalTime.parse(task.getCallStartTime())) && time.isBefore(LocalTime.parse(task.getCallEndTime())), "Outside call window");
    }
    public DialerCapacity capacity(Long company, Long taskId) {
        TaskConfig task = store.task(company, taskId);
        CompanyInfo owner = cache.getCompany(company);
        check(owner != null && Integer.valueOf(1).equals(owner.getStatus()), "Company unavailable");
        check(owner.getExpireTime()==null || owner.getExpireTime()<=0 || CdrTimes.epochMillis(owner.getExpireTime())>System.currentTimeMillis(),"Company subscription expired");
        TaskSource source = sources.selectByPrimaryKey(task.getSourceId());
        check(source != null && Objects.equals(company, source.getCompanyId()) && Integer.valueOf(1).equals(source.getStatus()), "Source unavailable");
        check(task.getTransferId() != null, "Missing target ID");
        check(fs.availableMedia() != null, "No media connection");
        display(task);
        boolean human = !"ivr".equals(task.getTransferType());
        DialerCapacity metrics = dialer.metrics(company, taskId, task.getTransferType(), task.getTransferId(), Instant.now().getEpochSecond()-3600);
        if ("group".equals(task.getTransferType())) {
            GroupInfo group = cache.getGroupInfo(task.getTransferId());
            check(group != null && Objects.equals(company,group.getCompanyId()) && Integer.valueOf(1).equals(group.getStatus()), "Group unavailable");
            List<String> online = group.getOnlineAgents();
            if (online != null) { metrics.setReadyAgents((int) online.stream().distinct().map(cache::getAgentInfo).filter(a -> ready(company,a)).count()); }
        } else if ("agent".equals(task.getTransferType())) {
            Agent target = agents.selectByPrimaryKey(task.getTransferId());
            check(target != null && Objects.equals(company,target.getCompanyId()) && Integer.valueOf(1).equals(target.getStatus()), "Agent unavailable");
            metrics.setReadyAgents(ready(company,cache.getAgentInfo(target.getAgentKey())) ? 1 : 0);
        } else if ("ivr".equals(task.getTransferType())) {
            IvrWorkflow ivr = ivrs.selectByPrimaryKey(task.getTransferId());
            check(ivr != null && Objects.equals(company,ivr.getCompanyId()) && Integer.valueOf(1).equals(ivr.getStatus()), "IVR unavailable");
            check("cruise".equals(task.getCruiseType()), "Predictive mode requires human target");
        } else { check(false,"Unsupported target"); }
        check("cruise".equals(task.getCruiseType()) || "predictive".equals(task.getCruiseType()), "Unsupported dialing mode");
        metrics.setAllowance(DialingPolicy.allowance(task.getCruiseType(), task.getConcurrency(), human, metrics,
                (owner.getOutboundLimit()!=null && owner.getOutboundLimit()>0?Math.min(properties.getCompanyConcurrency(),owner.getOutboundLimit()):properties.getCompanyConcurrency()), properties.getMaxPredictiveRatio(), properties.getMaxAbandonRate()));
        return metrics;
    }
    private static boolean ready(Long company, AgentInfo agent) {
        return agent != null && Objects.equals(company,agent.getCompanyId()) && Integer.valueOf(1).equals(agent.getStatus())
                && agent.getAgentState()==AgentState.READY && agent.getCallId()==null && Long.valueOf(0).equals(agent.getLogoutTime())
                && (agent.getTaskAfterUntil()==null || agent.getTaskAfterUntil()<=Instant.now().getEpochSecond());
    }
    private String display(TaskConfig task) {
        if (task.getNumberPoolId()!=null) {
            List<CompanyPhone> phones=dialer.displayPhones(task.getCompanyId(),task.getNumberPoolId());
            check(!phones.isEmpty(),"No enabled display numbers in pool");
            if (task.getDisplayNumber()!=null && !task.getDisplayNumber().isEmpty()) {
                check(phones.stream().anyMatch(p -> task.getDisplayNumber().equals(p.getPhone())),"Display number is outside task pool");
                return task.getDisplayNumber();
            }
            return phones.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(phones.size())).getPhone();
        }
        check(task.getDisplayNumber()!=null && task.getDisplayNumber().matches("[+0-9]{3,32}"),"Configure a display number or enabled number pool");
        return task.getDisplayNumber();
    }
    Destination destination(TaskConfig task, TaskContact contact) {
        check(contact.getPhone()!=null && contact.getPhone().matches("[+0-9]{3,32}"),"Invalid contact phone");
        String display=display(task);
        check(display.matches("[+0-9]{3,32}"),"Invalid display number");
        RouteGetway route=cache.getRouteGetway(task.getCompanyId(),contact.getPhone());
        check(route!=null,"No tenant route");
        String media=fs.availableMedia(); check(media!=null,"No media connection");
        return new Destination(media,route,display);
    }
    static class Destination {
        final String mediaHost; final RouteGetway route; final String display;
        Destination(String mediaHost,RouteGetway route,String display) { this.mediaHost=mediaHost; this.route=route; this.display=display; }
    }
    public DialerStatus dial(Long company, Long taskId, DialerRequest request) {
        TaskDialerStore.Reservation reserved=store.reserve(company,taskId,request,this);
        if (!reserved.fresh) { return status(company,taskId,request); }
        store.start(reserved,this);
        DialerStatus pending=new DialerStatus(); pending.setCallId(reserved.attempt.getCallId()); return pending;
    }
    void originate(TaskDialerStore.Reservation reserved) {
        Long company=reserved.task.getCompanyId(); Long taskId=reserved.task.getId();
        CallInfo call=new CallInfo();
        call.setCallId(reserved.attempt.getCallId()); call.setCompanyId(company); call.setTaskId(taskId);
        call.setCallType(CallType.AUTO_CALL); call.setDirection(Direction.OUTBOUND); call.setCallTime(System.currentTimeMillis());
        call.setCaller(reserved.display); call.setCallerDisplay(reserved.display); call.setCalled(reserved.contact.getPhone()); call.setCalledDisplay(reserved.display);
        call.setMediaHost(reserved.attempt.getMediaHost());
        call.setTaskTransferType(reserved.task.getTransferType()); call.setTaskTransferId(reserved.task.getTransferId());
        call.setTaskAfterDuration(reserved.task.getAfterCallDuration());
        if ("group".equals(reserved.task.getTransferType())) { call.setGroupId(reserved.task.getTransferId()); }
        CompanyInfo owner=cache.getCompany(company);
        check(owner!=null,"Company unavailable");
        call.setHiddenCustomer(Integer.valueOf(1).equals(owner.getHiddenCustomer())?1:0); call.setCdrNotifyUrl(owner.getNotifyUrl());
        if (reserved.contact.getCustomData()!=null) { call.setFollowData(JSON.parseObject(reserved.contact.getCustomData())); }
        DeviceInfo customer=new DeviceInfo(); customer.setCallId(call.getCallId()); customer.setDeviceId(reserved.attempt.getAttemptId());
        customer.setDeviceType(2); customer.setCdrType(2); customer.setCallTime(call.getCallTime());
        customer.setCaller(reserved.display); customer.setDisplay(reserved.display); customer.setCalled(reserved.contact.getPhone());
        call.getDeviceList().add(customer.getDeviceId()); call.getDeviceInfoMap().put(customer.getDeviceId(),customer);
        call.getNextCommands().add(new NextCommand(customer.getDeviceId(),NextType.NEXT_TASK,null));
        cache.addDevice(customer.getDeviceId(),call.getCallId()); cache.addCallInfo(call);
        CallLog initial=new CallLog(); org.springframework.beans.BeanUtils.copyProperties(call,initial);
        initial.setCallType(call.getCallType().name()); initial.setDirection(call.getDirection().name()); initial.setCts(call.getCallTime());
        cdr.saveLog(initial);
        // Connection ambiguity is deliberately NOT converted into another originate.
        fs.originateTask(call.getMediaHost(),reserved.route,reserved.display,reserved.contact.getPhone(),call.getCallId(),customer.getDeviceId(),
                reserved.task.getCallTimeout(),properties.getMaxCallSeconds()).whenComplete((event,error) -> {
            if (error==null && event!=null && String.join("",event.getEventBodyLines()).startsWith("-ERR")) {
                // A definite FreeSWITCH rejection can be finalized immediately on the next status reconciliation.
                CallInfo latest=cache.getCallInfo(call.getCallId());
                if (latest!=null) { latest.getProcessData().put("taskOriginateCause",String.join("",event.getEventBodyLines()).substring(4).trim()); cache.addCallInfo(latest); }
            }
        });
    }
    public DialerStatus status(Long company,Long taskId,DialerRequest request) {
        TaskConfig task=store.task(company,taskId);
        TaskAttempt attempt=store.attempt(company,taskId,request);
        DialerStatus status=new DialerStatus(); status.setCallId(attempt.getCallId());
        if (attempt.getCallId()==null) { return status; }
        CallLog log=dialer.call(company,taskId,attempt.getCallId());
        if (log==null || log.getEndTime()==null || log.getEndTime()<=0) {
            fs.withTaskEvents(attempt.getCallId(),() -> store.reconcile(company,taskId,request,this));
            log=dialer.call(company,taskId,attempt.getCallId());
        }
        if (log==null || log.getEndTime()==null || log.getEndTime()<=0) { return status; }
        CdrTimes.normalize(log);
        CallDevice customer=dialer.customer(company,attempt.getCallId()); CdrTimes.normalize(customer);
        boolean answered=(customer!=null && customer.getAnswerTime()!=null && customer.getAnswerTime()>0) || (log.getAnswerTime()!=null && log.getAnswerTime()>0);
        status.setCompleted(true); status.setResult(answered?"answered":result(customer==null?null:customer.getHangupCause()));
        status.setAnswerTime(answered?(customer!=null && customer.getAnswerTime()!=null && customer.getAnswerTime()>0?customer.getAnswerTime():log.getAnswerTime())/1000:null);
        status.setBridgeTime(!"ivr".equals(task.getTransferType()) && log.getAnswerFlag()!=null && log.getAnswerFlag()==0 && log.getAnswerTime()!=null && log.getAnswerTime()>0?log.getAnswerTime()/1000:null);
        Agent assigned=log.getAgentKey()==null || log.getAgentKey().isEmpty()?null:agents.selectAgent(log.getAgentKey());
        status.setAgentCode(assigned!=null && Objects.equals(company,assigned.getCompanyId())?assigned.getAgentId():null); return status;
    }
    void reconcile(TaskConfig task,TaskAttempt attempt) {
        long elapsed=Instant.now().getEpochSecond()-attempt.getDispatchTime();
        CallInfo call=cache.getCallInfo(attempt.getCallId());
        if (call!=null) { for (String id:call.getDeviceInfoMap().keySet()) { cache.addDevice(id,call.getCallId()); } }
        if (elapsed<task.getCallTimeout()+30L && (call==null || !call.getProcessData().containsKey("taskOriginateCause"))) { return; }
        List<String> devices=call==null?fs.taskChannels(attempt.getMediaHost(),attempt.getCallId()):new ArrayList<>(call.getDeviceInfoMap().keySet());
        if (call==null && !devices.contains(attempt.getAttemptId())) { devices.add(attempt.getAttemptId()); }
        boolean active=false;
        for (String device:devices) {
            if (fs.taskChannelExists(attempt.getMediaHost(),device)) {
                active=true;
                if (call==null || elapsed>properties.getMaxCallSeconds()+task.getCallTimeout()+30L) {
                    fs.hangupCall(attempt.getMediaHost(),attempt.getCallId(),device);
                }
            }
        }
        if (active) { return; }
        // No originate is retried. Every known channel has been confirmed absent on a connected media node.
        if (call==null) {
            call=new CallInfo();
            CallLog previous=dialer.call(task.getCompanyId(),task.getId(),attempt.getCallId());
            if (previous!=null) { org.springframework.beans.BeanUtils.copyProperties(previous,call); }
            CallDevice endedCustomer=dialer.customer(task.getCompanyId(),attempt.getCallId());
            if (endedCustomer!=null) {
                DeviceInfo device=new DeviceInfo(); org.springframework.beans.BeanUtils.copyProperties(endedCustomer,device);
                call.getDeviceInfoMap().put(device.getDeviceId(),device);
            }
            call.setCallId(attempt.getCallId()); call.setCompanyId(task.getCompanyId()); call.setTaskId(task.getId());
            call.setCallTime(attempt.getDispatchTime()*1000); call.setCallType(CallType.AUTO_CALL); call.setDirection(Direction.OUTBOUND);
            call.setMediaHost(attempt.getMediaHost());
            call.setTaskTransferType(task.getTransferType()); call.setTaskTransferId(task.getTransferId());
            call.setTaskAfterDuration(task.getAfterCallDuration());
            if (call.getGroupId()==null && "group".equals(task.getTransferType())) { call.setGroupId(task.getTransferId()); }
            CompanyInfo owner=cache.getCompany(task.getCompanyId());
            if (owner!=null) { call.setCdrNotifyUrl(owner.getNotifyUrl()); }
        }
        call.setEndTime(System.currentTimeMillis());
        for (DeviceInfo device:call.getDeviceInfoMap().values()) {
            if (device.getEndTime()==null) { device.setEndTime(call.getEndTime()); device.setHangupCause(call.getProcessData().containsKey("taskOriginateCause")?String.valueOf(call.getProcessData().get("taskOriginateCause")):"RECOVERY_TIMEOUT"); }
        }
        com.voxai.cc.cdr.CdrCompletion completion=CdrAssembler.complete(call,call.getCdrNotifyUrl());
        completion.setNotifyVersion(notifyVersion);
        cdr.complete(completion);
        if (call.getAgentKey()!=null) {
            AgentInfo agent=cache.getAgentInfo(call.getAgentKey());
            if (agent!=null && Objects.equals(call.getCallId(),agent.getCallId())) { after.begin(call,agent); }
        }
        groups.removeCall(call.getGroupId(),call.getCallId());
        cache.removeCallInfo(call.getCallId());
    }
    static String result(String cause) {
        if ("USER_BUSY".equals(cause)) { return "busy"; }
        if ("CALL_REJECTED".equals(cause)) { return "rejected"; }
        if (Arrays.asList("UNALLOCATED_NUMBER","INVALID_NUMBER_FORMAT","NO_ROUTE_DESTINATION").contains(cause)) { return "invalid"; }
        if (Arrays.asList("NO_ANSWER","NO_USER_RESPONSE","ALLOTTED_TIMEOUT").contains(cause)) { return "no_answer"; }
        return "failed";
    }
}
