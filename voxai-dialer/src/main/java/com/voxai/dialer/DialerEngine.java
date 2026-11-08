package com.voxai.dialer;

import com.voxai.core.entity.TaskConfig;
import com.voxai.core.po.dialer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.*;

/** Stateless scheduler: each tick reconstructs work from durable attempts, including paused/closed tasks. */
@Component
@ConditionalOnProperty(name="dialer.enabled",havingValue="true")
public class DialerEngine {
    private static final Logger log=LoggerFactory.getLogger(DialerEngine.class);
    private final DialerGateway gateway;
    private final DialerProperties properties;
    public DialerEngine(DialerGateway gateway,DialerProperties properties) { this.gateway=gateway; this.properties=properties; }
    @Scheduled(fixedDelayString="${dialer.poll-millis:1000}")
    public void tick() {
        try {
            long after=0;
            while (true) {
                List<TaskConfig> tasks=gateway.tasks(after);
                for (TaskConfig task:tasks) {
                    if (task.getId()<=after) { throw new IllegalStateException("Unordered task cursor"); }
                    try { process(task); }
                    catch (RuntimeException e) { log.warn("Dialer task {} deferred ({})",task.getId(),e.getClass().getSimpleName()); }
                    after=task.getId();
                }
                if (tasks.size()<100) { break; }
            }
        } catch (RuntimeException e) { log.warn("Dialer polling deferred ({})",e.getClass().getSimpleName()); }
    }
    private void process(TaskConfig task) {
        recover(task);
        if (task.getRequestedStatus()!=null) {
            if (task.getRequestedStatus()==1) { gateway.capacity(task.getId()); }
            // Pausing/closing is graceful. The DB request already fences every new dispatch.
            gateway.acknowledge(task,true); return;
        }
        if (!Integer.valueOf(1).equals(task.getStatus())) { return; }
        if (task.getCloseTime()==null || task.getCloseTime()<=Instant.now().getEpochSecond() || gateway.pending(task.getId())==0) {
            gateway.close(task); return;
        }
        int slots=Math.min(properties.getBatchSize(),gateway.capacity(task.getId()).getAllowance());
        for (int i=0;i<slots;i++) {
            List<DialerWork> work=gateway.claim(task.getId(),UUID.randomUUID().toString());
            if (work.isEmpty()) { break; }
            for (DialerWork item:work) { gateway.dial(item); }
        }
    }
    private void recover(TaskConfig task) {
        String after="";
        while (true) {
            List<DialerWork> work=gateway.active(task.getId(),after);
            for (DialerWork item:work) {
                if (item.getAttemptId()==null || item.getAttemptId().compareTo(after)<=0) { throw new IllegalStateException("Unordered attempt cursor"); }
                try {
                    if (item.getCallId()!=null) {
                        gateway.heartbeat(item);
                        DialerStatus status=gateway.status(item);
                        if (status.isCompleted()) { gateway.complete(item,status); }
                    } else if (Integer.valueOf(1).equals(task.getStatus()) && task.getRequestedStatus()==null
                            && item.getLeaseUntil()>Instant.now().getEpochSecond()) {
                        gateway.dial(item);
                    }
                } catch (RuntimeException e) { log.warn("Dialer attempt {} deferred ({})",item.getAttemptId(),e.getClass().getSimpleName()); }
                after=item.getAttemptId();
            }
            if (work.size()<100) { break; }
        }
    }
}
