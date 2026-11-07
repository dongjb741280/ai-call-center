package com.voxai.api.service;

import com.voxai.api.vo.task.TaskClaim;
import com.voxai.core.entity.Group;
import com.voxai.core.entity.TaskConfig;
import com.voxai.core.entity.TaskSource;
import com.voxai.core.mapper.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TaskManagementServiceTest {
    @Test
    void doesNotClaimWhenTaskConcurrencyIsAlreadyFull() {
        TaskManagementService service = new TaskManagementService();
        TaskManagementMapper tasks = mock(TaskManagementMapper.class);
        TaskSourceMapper sources = mock(TaskSourceMapper.class);
        GroupMapper groups = mock(GroupMapper.class);
        ReflectionTestUtils.setField(service, "tasks", tasks);
        ReflectionTestUtils.setField(service, "sources", sources);
        ReflectionTestUtils.setField(service, "fields", mock(TaskFieldMapper.class));
        ReflectionTestUtils.setField(service, "groups", groups);
        ReflectionTestUtils.setField(service, "ivrs", mock(IvrWorkflowMapper.class));
        ReflectionTestUtils.setField(service, "agents", mock(AgentMapper.class));
        ReflectionTestUtils.setField(service, "pools", mock(CompanyDisplayMapper.class));

        TaskConfig task = new TaskConfig();
        task.setId(11L);
        task.setCompanyId(7L);
        task.setStatus(1);
        task.setRequestedStatus(null);
        task.setSourceId(20L);
        task.setTransferId(30L);
        task.setTransferType("group");
        task.setConcurrency(1);
        task.setCallRounds(1);
        task.setCloseTime(Instant.now().getEpochSecond() + 600);
        task.setCallStartTime("00:00:00");
        task.setCallEndTime("23:59:59");
        when(tasks.lockTask(7L, 11L)).thenReturn(task);
        TaskSource source = new TaskSource();
        source.setStatus(1);
        when(tasks.lockSource(7L, 20L)).thenReturn(source);
        Group target = new Group();
        target.setId(30L);
        target.setCompanyId(7L);
        target.setStatus(1);
        when(groups.selectByPrimaryKey(30L)).thenReturn(target);
        when(tasks.attemptByRequest(7L, "request_123456789")).thenReturn(null);
        when(tasks.countActiveContacts(7L, 11L)).thenReturn(1);

        TaskClaim claim = new TaskClaim();
        claim.setRequestId("request_123456789");
        claim.setLimit(1);
        claim.setLeaseSeconds(30);
        assertTrue(service.claim(7L, 11L, claim).isEmpty());
        verify(tasks, never()).claimContacts(anyLong(), anyLong(), anyLong(), anyInt());
    }
}
