package com.voxai.core.mapper;
import com.voxai.core.entity.*;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface TaskManagementMapper {
    TaskConfig lockTask(@Param("companyId") Long companyId, @Param("id") Long id);
    TaskConfig selectTaskForCall(@Param("companyId") Long companyId, @Param("id") Long id);
    TaskSource lockSource(@Param("companyId") Long companyId, @Param("id") Long id);
    TaskField lockField(@Param("companyId") Long companyId, @Param("id") Long id);
    int insertTask(TaskConfig task);
    int editTask(TaskConfig task);
    int archiveTask(@Param("companyId") Long companyId, @Param("id") Long id);
    int issueCommand(TaskConfig task);
    int acknowledgeCommand(TaskConfig task);
    int bumpSource(@Param("companyId") Long companyId, @Param("id") Long id, @Param("version") Long version);
    int clearFields(@Param("companyId") Long companyId, @Param("sourceId") Long sourceId);
    int bindField(@Param("companyId") Long companyId, @Param("sourceId") Long sourceId, @Param("id") Long id);
    int sourceReferences(@Param("companyId") Long companyId, @Param("id") Long id);
    int deleteSource(@Param("companyId") Long companyId, @Param("id") Long id);
    int deleteField(@Param("companyId") Long companyId, @Param("id") Long id);
    List<TaskContact> contacts(@Param("companyId") Long companyId, @Param("taskId") Long taskId, @Param("callStatus") String callStatus, @Param("round") Integer round);
    List<TaskPause> pauses(@Param("companyId") Long companyId, @Param("taskId") Long taskId);
    int insertContact(TaskContact contact);
    int countPhoneFields(@Param("companyId") Long companyId, @Param("sourceId") Long sourceId);
    int countDuplicateContact(@Param("taskId") Long taskId, @Param("uuid") String uuid);
    int releaseExpired(@Param("companyId") Long companyId, @Param("taskId") Long taskId, @Param("now") Long now);
    int expireAttempts(@Param("companyId") Long companyId, @Param("taskId") Long taskId, @Param("now") Long now);
    int openPause(@Param("id") Long taskId, @Param("now") Long now);
    int closePause(@Param("id") Long taskId, @Param("now") Long now);
    List<TaskContact> claimContacts(@Param("companyId") Long companyId, @Param("taskId") Long taskId, @Param("now") Long now, @Param("limit") int limit);
    int countActiveContacts(@Param("companyId") Long companyId, @Param("taskId") Long taskId);
    int countActiveAttempts(@Param("companyId") Long companyId, @Param("taskId") Long taskId);
    int countTaskCall(@Param("companyId") Long companyId, @Param("taskId") Long taskId, @Param("callId") Long callId);
    int countCallAttempts(@Param("companyId") Long companyId, @Param("taskId") Long taskId, @Param("callId") Long callId);
    int leaseContact(@Param("contactId") Long contactId, @Param("taskId") Long taskId, @Param("attemptId") String attemptId, @Param("leaseUntil") Long leaseUntil, @Param("now") Long now);
    int insertAttempt(TaskAttempt attempt);
    TaskAttempt attemptByRequest(@Param("companyId") Long companyId, @Param("requestId") String requestId);
    TaskContact contactByAttempt(@Param("attemptId") String attemptId);
    int renewAttempt(@Param("companyId") Long companyId, @Param("taskId") Long taskId,
                     @Param("attemptId") String attemptId, @Param("leaseToken") String leaseToken,
                     @Param("leaseUntil") Long leaseUntil, @Param("now") Long now);
    int completeAttempt(TaskAttempt attempt);
    int completeContact(TaskContact contact);
    int updateTaskCounters(@Param("companyId") Long companyId, @Param("id") Long taskId, @Param("now") Long now);
}
