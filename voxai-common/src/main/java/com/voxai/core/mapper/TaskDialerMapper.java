package com.voxai.core.mapper;

import com.voxai.core.entity.*;
import com.voxai.core.po.dialer.*;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface TaskDialerMapper {
    List<TaskConfig> engineTasks(@Param("companyId") Long companyId, @Param("afterId") Long afterId, @Param("limit") int limit);
    List<DialerWork> activeAttempts(@Param("companyId") Long companyId, @Param("taskId") Long taskId,
                                    @Param("afterAttemptId") String afterAttemptId, @Param("limit") int limit);
    TaskAttempt lockAttempt(@Param("companyId") Long companyId, @Param("taskId") Long taskId, @Param("attemptId") String attemptId);
    Long lockCompany(@Param("companyId") Long companyId);
    int pendingContacts(@Param("companyId") Long companyId, @Param("taskId") Long taskId);
    int assignCall(TaskAttempt attempt);
    DialerCapacity metrics(@Param("companyId") Long companyId, @Param("taskId") Long taskId,
                           @Param("transferType") String transferType, @Param("transferId") Long transferId, @Param("since") Long since);
    List<CompanyPhone> displayPhones(@Param("companyId") Long companyId, @Param("poolId") Long poolId);
    CallLog call(@Param("companyId") Long companyId, @Param("taskId") Long taskId, @Param("callId") Long callId);
    CallDevice customer(@Param("companyId") Long companyId, @Param("callId") Long callId);
}
