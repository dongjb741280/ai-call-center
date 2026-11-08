package com.voxai.cc.dialer;

import com.voxai.core.entity.*;
import com.voxai.core.mapper.*;
import com.voxai.core.po.dialer.DialerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Objects;

/** Commit the dispatch decision before any external media side effect. Company -> task -> attempt lock order. */
@Service
public class TaskDialerStore {
    private final TaskManagementMapper tasks;
    private final TaskDialerMapper dialer;
    public TaskDialerStore(TaskManagementMapper tasks, TaskDialerMapper dialer) { this.tasks=tasks; this.dialer=dialer; }
    static void check(boolean condition, String message) {
        if (!condition) { throw new ResponseStatusException(HttpStatus.CONFLICT, message); }
    }
    public TaskConfig task(Long company, Long taskId) {
        TaskConfig task = tasks.lockTask(company, taskId);
        check(task != null, "Task unavailable"); return task;
    }
    public TaskAttempt attempt(Long company, Long taskId, DialerRequest request) {
        check(request != null && request.getAttemptId() != null && request.getLeaseToken() != null, "Missing attempt credentials");
        TaskAttempt attempt = dialer.lockAttempt(company, taskId, request.getAttemptId());
        check(attempt != null && Objects.equals(request.getLeaseToken(), attempt.getLeaseToken()), "Attempt unavailable");
        return attempt;
    }
    @Transactional
    public Reservation reserve(Long company, Long taskId, DialerRequest request, TaskMediaService media) {
        check(dialer.lockCompany(company) != null, "Company disabled");
        TaskConfig task = task(company, taskId);
        TaskAttempt attempt = attempt(company, taskId, request);
        if (attempt.getCallId() != null) { return new Reservation(task, attempt, false, null, null, null); }
        long now = java.time.Instant.now().getEpochSecond();
        check("claimed".equals(attempt.getState()) && attempt.getLeaseUntil() > now, "Lease expired");
        check(Integer.valueOf(1).equals(task.getStatus()) && task.getRequestedStatus() == null, "Task not running");
        media.checkWindow(task, now);
        check(media.capacity(company, taskId).getAllowance() > 0, "No dial capacity");
        TaskContact contact = tasks.contactByAttempt(attempt.getAttemptId());
        check(contact != null && Objects.equals(contact.getTaskId(), taskId) && Objects.equals(contact.getActiveAttemptId(), attempt.getAttemptId()), "Contact changed");
        TaskMediaService.Destination destination = media.destination(task, contact);
        attempt.setCallId(media.nextCallId()); attempt.setMediaHost(destination.mediaHost); attempt.setDispatchTime(now); attempt.setUts(now);
        check(dialer.assignCall(attempt) == 1, "Dispatch already reserved");
        return new Reservation(task, attempt, true, contact, destination.route, destination.display);
    }
    @Transactional
    public void start(Reservation reserved, TaskMediaService media) {
        Long company=reserved.task.getCompanyId(); Long taskId=reserved.task.getId();
        check(dialer.lockCompany(company)!=null,"Company disabled");
        TaskConfig task=task(company,taskId);
        TaskAttempt current=dialer.lockAttempt(company,taskId,reserved.attempt.getAttemptId());
        long now=java.time.Instant.now().getEpochSecond();
        check(current!=null && "claimed".equals(current.getState()) && current.getLeaseUntil()>now
                && Objects.equals(current.getCallId(),reserved.attempt.getCallId()) && now-current.getDispatchTime()<30,"Dispatch reservation expired");
        check(Integer.valueOf(1).equals(task.getStatus()) && task.getRequestedStatus()==null,"Task stopped before dispatch");
        media.checkWindow(task,now);
        media.originate(reserved);
    }
    @Transactional
    public void reconcile(Long company,Long taskId,DialerRequest request,TaskMediaService media) {
        check(dialer.lockCompany(company)!=null,"Company unavailable for reconciliation");
        TaskConfig task=task(company,taskId); TaskAttempt attempt=attempt(company,taskId,request);
        CallLog log=dialer.call(company,taskId,attempt.getCallId());
        if (attempt.getCallId()!=null && (log==null || log.getEndTime()==null || log.getEndTime()<=0)) { media.reconcile(task,attempt); }
    }
    public static class Reservation {
        final TaskConfig task; final TaskAttempt attempt; final boolean fresh;
        final TaskContact contact; final RouteGetway route; final String display;
        Reservation(TaskConfig task, TaskAttempt attempt, boolean fresh, TaskContact contact, RouteGetway route, String display) {
            this.task=task; this.attempt=attempt; this.fresh=fresh; this.contact=contact; this.route=route; this.display=display;
        }
    }
}
