package com.voxai.dialer;

import com.voxai.core.entity.TaskConfig;
import com.voxai.core.po.dialer.*;
import java.util.List;

public interface DialerGateway {
    List<TaskConfig> tasks(long afterId);
    List<DialerWork> active(Long taskId,String afterAttemptId);
    DialerCapacity capacity(Long taskId);
    void acknowledge(TaskConfig task,boolean success);
    void close(TaskConfig task);
    int pending(Long taskId);
    List<DialerWork> claim(Long taskId,String requestId);
    void heartbeat(DialerWork work);
    DialerStatus dial(DialerWork work);
    DialerStatus status(DialerWork work);
    void complete(DialerWork work,DialerStatus result);
}
