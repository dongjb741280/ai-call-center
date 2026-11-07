package com.voxai.api.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.voxai.api.exception.BusinessException;
import com.voxai.api.vo.task.*;
import com.voxai.core.entity.*;
import com.voxai.core.enums.ErrorCode;
import com.voxai.core.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;

/** All task mutations enter here: tenant scope, row locks and transactions are mandatory. */
@Service
@Transactional
public class TaskManagementService {
    @Autowired private TaskManagementMapper tasks;
    @Autowired private TaskSourceMapper sources;
    @Autowired private TaskFieldMapper fields;
    @Autowired private GroupMapper groups;
    @Autowired private IvrWorkflowMapper ivrs;
    @Autowired private AgentMapper agents;
    @Autowired private CompanyDisplayMapper pools;

    public TaskConfig task(Long company, Long id) {
        require(company != null && company > 0, "企业不能为空");
        TaskConfig task = tasks.lockTask(company, id);
        require(task != null, "任务不存在或不属于当前企业");
        return task;
    }
    public TaskSource source(Long company, Long id) {
        TaskSource source = tasks.lockSource(company, id);
        require(source != null, "数据源不存在或不属于当前企业");
        return source;
    }
    public TaskField field(Long company, Long id) {
        TaskField field = tasks.lockField(company, id);
        require(field != null, "字段不存在或不属于当前企业");
        return field;
    }
    public TaskConfig saveTask(Long company, Long id, TaskEdit edit) {
        require(edit != null, "配置不能为空");
        TaskConfig previous = id == null ? null : task(company, id);
        if (previous != null) {
            require(Integer.valueOf(2).equals(previous.getStatus()) && previous.getRequestedStatus() == null, "仅已暂停且没有待确认命令的任务可编辑");
            require(Objects.equals(edit.getVersion(), previous.getVersion()), "任务已变化，请刷新版本");
        }
        TaskConfig value = new TaskConfig();
        BeanUtils.copyProperties(edit, value);
        value.setId(id); value.setCompanyId(company);
        require(edit.getName() != null && !edit.getName().trim().isEmpty() && edit.getName().length() <= 64, "任务名称长度为1至64");
        try {
            LocalTime start = LocalTime.parse(edit.getCallStartTime());
            LocalTime end = LocalTime.parse(edit.getCallEndTime());
            require(start.isBefore(end), "时间窗必须开始早于结束，跨日请拆分任务");
        } catch (java.time.DateTimeException | NullPointerException e) { throw invalid("时间格式必须为HH:mm:ss"); }
        require(edit.getCallStartTime().length() == 8 && edit.getCallEndTime().length() == 8, "时间格式必须为HH:mm:ss");
        require(edit.getCloseTime() != null && edit.getCloseTime() > now() && edit.getCloseTime() < 100000000000L, "关闭时间必须为未来的秒级时间戳");
        range(edit.getConcurrency(), 1, 10000, "并发"); range(edit.getCallRounds(), 1, 100, "轮次");
        range(edit.getCallTimeout(), 1, 600, "呼叫超时（秒）"); range(edit.getAfterCallDuration(), 0, 3600, "话后时长（秒）");
        if (value.getRetryInterval() == null) { value.setRetryInterval(300); }
        range(value.getRetryInterval(), 1, 86400, "重拨间隔（秒）");
        require(Arrays.asList("predictive", "cruise").contains(edit.getCruiseType()), "cruiseType只允许predictive或cruise");
        TaskSource source = source(company, edit.getSourceId());
        require(Integer.valueOf(1).equals(source.getStatus()), "数据源未启用");
        value.setDataSource(source.getName());
        resolveTarget(company, value);
        if (edit.getNumberPoolId() != null) {
            CompanyDisplay pool = pools.selectByPrimaryKey(edit.getNumberPoolId());
            require(pool != null && Objects.equals(company, pool.getCompanyId()) && Integer.valueOf(1).equals(pool.getStatus()), "号码池不存在、已停用或不属于当前企业");
            value.setNumberPoolName(pool.getName());
        }
        if (edit.getDisplayNumber() != null) { require(edit.getDisplayNumber().matches("[+0-9]{3,32}"), "外显号码格式错误"); }
        url(edit.getStatusNotify()); url(edit.getTrafficNotify());
        value.setUts(now());
        if (id == null) {
            value.setCts(now()); value.setStatus(2); value.setVersion(0L); value.setArchived(0);
            tasks.insertTask(value);
        } else {
            require(previous.getSourceId() == null || Objects.equals(previous.getSourceId(), value.getSourceId()), "已创建任务不可切换数据源，请新建任务");
            require(tasks.editTask(value) == 1, "任务已变化，请刷新");
        }
        return task(company, value.getId());
    }
    private void resolveTarget(Long company, TaskConfig value) {
        require(value.getTransferId() != null, "必须指定稳定的转接目标ID");
        String type = value.getTransferType();
        if ("group".equals(type)) {
            Group target=groups.selectByPrimaryKey(value.getTransferId());
            require(target != null && Objects.equals(company,target.getCompanyId()) && Integer.valueOf(1).equals(target.getStatus()), "技能组不可用");
            value.setTransferName(target.getName());
        } else if ("ivr".equals(type)) {
            IvrWorkflow target=ivrs.selectByPrimaryKey(value.getTransferId());
            require(target != null && Objects.equals(company,target.getCompanyId()) && Integer.valueOf(1).equals(target.getStatus()), "IVR不可用");
            value.setTransferName(target.getName());
        } else if ("agent".equals(type)) {
            Agent target=agents.selectByPrimaryKey(value.getTransferId());
            require(target != null && Objects.equals(company,target.getCompanyId()) && Integer.valueOf(1).equals(target.getStatus()), "坐席不可用");
            value.setTransferName(target.getAgentName());
        } else { throw invalid("transferType只允许group、ivr或agent"); }
    }
    public TaskConfig command(Long company, Long id, TaskCommand command) {
        TaskConfig task=task(company,id);
        require(command != null && command.getCommandId() != null && command.getCommandId().matches("[A-Za-z0-9_-]{16,64}"), "commandId必须为16至64位唯一请求标识");
        Integer target = "start".equals(command.getAction()) || "resume".equals(command.getAction()) ? 1 : "pause".equals(command.getAction()) ? 2 : "close".equals(command.getAction()) ? 3 : null;
        require(target != null, "未知任务命令");
        if (command.getCommandId().equals(task.getCommandId())) {
            require(Objects.equals(target, task.getRequestedStatus() == null ? task.getStatus() : task.getRequestedStatus()), "同一命令ID不可复用于不同动作");
            return task;
        }
        require(Objects.equals(command.getVersion(),task.getVersion()), "任务已变化，请刷新版本");
        require(task.getRequestedStatus() == null && task.getStatus() != 3 && !Objects.equals(target,task.getStatus()), "当前状态不可执行该命令");
        if (target == 1) {
            require(task.getSourceId()!=null && task.getTransferId()!=null && task.getCloseTime()!=null && task.getCloseTime()>now(), "请先补全稳定关联与有效关闭时间");
            resolveTarget(company,task);
            require(Integer.valueOf(1).equals(source(company,task.getSourceId()).getStatus()), "数据源不可用");
        }
        task.setRequestedStatus(target); task.setCommandId(command.getCommandId()); task.setCommandTime(now()); task.setUts(now());
        require(tasks.issueCommand(task)==1, "任务已变化，请刷新");
        return task(company,id);
    }
    public TaskConfig acknowledge(Long company, Long id, String commandId, boolean success) {
        TaskConfig task=task(company,id);
        require(Objects.equals(commandId,task.getCommandId()), "命令已过期或不存在");
        if (task.getRequestedStatus()==null) { return task; }
        int old=task.getStatus();
        if(success) { task.setStatus(task.getRequestedStatus()); }
        if(success && task.getStatus()==2) { task.setPauseStartTime(now()); task.setPauseEndTime(null); tasks.openPause(id,now()); }
        if(success && old==2 && task.getStatus()!=2) { task.setPauseEndTime(now()); tasks.closePause(id,now()); }
        task.setUts(now()); require(tasks.acknowledgeCommand(task)==1, "命令已变化");
        return task(company,id);
    }
    public void archive(Long company,Long id) {
        TaskConfig task=task(company,id);
        require(task.getStatus()==3 && task.getRequestedStatus()==null, "须等待引擎确认关闭后归档");
        long current = now();
        tasks.releaseExpired(company, id, current);
        tasks.expireAttempts(company, id, current);
        require(tasks.countActiveAttempts(company, id)==0, "仍有未完成拨打，请等待结果回写或租约到期后归档");
        require(tasks.archiveTask(company,id)==1, "归档失败");
    }
    public void bind(Long company,Long sourceId,TaskFieldsEdit edit) {
        TaskSource source=source(company,sourceId);
        require(edit!=null && edit.getFieldIds()!=null && edit.getFieldIds().size()<=200, "字段列表必须显式提供，最多200个");
        require(Objects.equals(edit.getVersion(),source.getVersion()), "数据源已变化，请刷新版本");
        List<Long> ids=new ArrayList<>(new TreeSet<>(edit.getFieldIds()));
        require(ids.size()==edit.getFieldIds().size(), "字段ID不可重复");
        for(Long id:ids) {
            TaskField field=field(company,id);
            require(field.getSourceId()==null || field.getSourceId()==0 || Objects.equals(field.getSourceId(),sourceId), "字段已归属其他数据源，不能隐式移动");
        }
        require(tasks.bumpSource(company,sourceId,edit.getVersion())==1, "数据源已变化");
        tasks.clearFields(company,sourceId);
        for(Long id:ids) { require(tasks.bindField(company,sourceId,id)==1, "字段已变化"); }
    }
    public TaskSource saveSource(Long company,Long id,TaskSource input) {
        require(input!=null && input.getName()!=null && !input.getName().trim().isEmpty() && input.getName().length()<=64, "数据源名称长度为1至64");
        require(input.getStatus()==null || input.getStatus()==0 || input.getStatus()==1, "数据源状态错误");
        TaskSource value=new TaskSource(); value.setName(input.getName()); value.setStatus(input.getStatus()==null?1:input.getStatus()); value.setCompanyId(company);
        if(id==null) { value.setCts(now()); value.setVersion(0L); sources.insertSelective(value); return value; }
        TaskSource old=source(company,id); require(Objects.equals(input.getVersion(),old.getVersion()), "数据源已变化，请刷新版本");
        value.setId(id); value.setUts(now()); require(tasks.bumpSource(company,id,input.getVersion())==1, "数据源已变化"); sources.updateByPrimaryKeySelective(value); return source(company,id);
    }
    public void deleteSource(Long company,Long id) {
        source(company,id); require(tasks.sourceReferences(company,id)==0, "数据源已被任务引用，不能删除");
        tasks.clearFields(company,id); tasks.deleteSource(company,id);
    }
    public TaskField saveField(Long company,Long id,TaskField input) {
        require(input!=null && input.getName()!=null && !input.getName().trim().isEmpty() && input.getName().length()<=64, "字段名称长度为1至64");
        require(input.getIsPhone()!=null && (input.getIsPhone()==0 || input.getIsPhone()==1), "isPhone必须为0或1");
        require(input.getStatus()==null || input.getStatus()==0 || input.getStatus()==1, "字段状态错误");
        require(Arrays.asList("系统字段","自定义字段").contains(input.getFieldType()), "字段类型错误");
        TaskField value=new TaskField(); value.setCompanyId(company); value.setName(input.getName()); value.setIsPhone(input.getIsPhone()); value.setStatus(input.getStatus()==null?1:input.getStatus()); value.setFieldType(input.getFieldType());
        if(id==null) { value.setCts(now()); fields.insertSelective(value); }
        else { field(company,id); value.setId(id); value.setUts(now()); fields.updateByPrimaryKeySelective(value); }
        return value;
    }
    public void deleteField(Long company,Long id) {
        TaskField field=field(company,id); require(field.getSourceId()==null || field.getSourceId()==0, "请先解绑字段");
        require(tasks.deleteField(company,id)==1, "字段已变化");
    }
    public PageInfo<TaskContact> contacts(Long company,Long id,int page,int size,String status,Integer round) {
        task(company,id); require(page>0 && size>0 && size<=200, "pageNum至少1，pageSize为1至200");
        PageHelper.startPage(page,size); return new PageInfo<>(tasks.contacts(company,id,status,round));
    }
    public List<TaskPause> pauses(Long company,Long id) { task(company,id); return tasks.pauses(company,id); }
    public List<TaskField> sourceFields(Long company,Long id) { source(company,id); return fields.selectBySourceId(id); }
    public int importContacts(Long company,Long id,TaskImport input) {
        TaskConfig task=task(company,id); require(task.getStatus()==2 && task.getRequestedStatus()==null, "仅暂停任务可导入");
        require(input!=null && input.getContacts()!=null && !input.getContacts().isEmpty() && input.getContacts().size()<=500, "每批名单数量为1至500");
        source(company,task.getSourceId());
        require(tasks.countPhoneFields(company,task.getSourceId())>0, "数据源必须绑定至少一个有效号码字段");
        for(TaskContact inputRow:input.getContacts()) {
            require(inputRow!=null && inputRow.getPhone()!=null && inputRow.getPhone().matches("[+0-9]{3,32}"), "名单号码格式错误");
            require(inputRow.getUuid()!=null && inputRow.getUuid().length()<=64 && !inputRow.getUuid().trim().isEmpty(), "每条名单必须提供稳定uuid");
            require(inputRow.getName()==null || inputRow.getName().length()<=64, "姓名过长");
            require(tasks.countDuplicateContact(id,inputRow.getUuid())==0, "名单uuid已存在");
            if(inputRow.getCustomData()!=null) { require(inputRow.getCustomData().length()<=16384, "自定义数据最多16KB"); try { require(JSON.parse(inputRow.getCustomData()) instanceof Map, "customData必须为JSON对象"); } catch(RuntimeException e) { throw invalid("customData必须为JSON对象"); } }
            TaskContact row=new TaskContact(); row.setTaskId(id); row.setSourceId(task.getSourceId()); row.setName(inputRow.getName()); row.setPhone(inputRow.getPhone()); row.setUuid(inputRow.getUuid()); row.setCustomData(inputRow.getCustomData()); tasks.insertContact(row);
        }
        return input.getContacts().size();
    }
    public List<com.voxai.api.vo.task.TaskClaimResult> claim(Long company,Long taskId,TaskClaim claim) {
        TaskConfig task=task(company,taskId);
        require(task.getStatus()==1 && task.getRequestedStatus()==null,"任务未处于已确认运行状态");
        require(claim!=null && claim.getRequestId()!=null && claim.getRequestId().matches("[A-Za-z0-9_-]{16,64}"),"requestId必须为16至64位唯一值");
        range(claim.getLimit(),1,1,"本接口每次只领取一条名单"); range(claim.getLeaseSeconds(),10,300,"租约秒数");
        long current = now();
        tasks.releaseExpired(company, taskId, current);
        tasks.expireAttempts(company, taskId, current);
        require(task.getSourceId() != null && task.getTransferId() != null, "旧任务须补齐数据源和转接目标后才能领取名单");
        require(Integer.valueOf(1).equals(source(company, task.getSourceId()).getStatus()), "任务数据源已停用");
        resolveTarget(company, task);
        TaskAttempt prior=tasks.attemptByRequest(company,claim.getRequestId());
        if(prior!=null) {
            require(Objects.equals(prior.getTaskId(),taskId),"requestId已用于其他任务");
            if ("claimed".equals(prior.getState()) && prior.getLeaseUntil() != null && prior.getLeaseUntil() > current) {
                TaskContact contact=tasks.contactByAttempt(prior.getAttemptId());
                require(contact!=null,"幂等领取记录关联名单缺失");
                return Collections.singletonList(claimResult(taskId,prior,contact));
            }
            return Collections.emptyList();
        }
        if (task.getCloseTime() == null || task.getCloseTime() <= current || task.getCallStartTime() == null || task.getCallEndTime() == null) {
            return Collections.emptyList();
        }
        LocalTime localTime = LocalTime.now();
        if (localTime.isBefore(LocalTime.parse(task.getCallStartTime())) || !localTime.isBefore(LocalTime.parse(task.getCallEndTime()))) {
            return Collections.emptyList();
        }
        if (task.getConcurrency() == null || task.getConcurrency() <= 0 || tasks.countActiveContacts(company, taskId) >= task.getConcurrency()) {
            return Collections.emptyList();
        }
        List<TaskContact> candidates=tasks.claimContacts(company,taskId,current,claim.getLimit());
        List<com.voxai.api.vo.task.TaskClaimResult> claimed=new ArrayList<>();
        for(TaskContact contact:candidates) {
            String attemptId=UUID.randomUUID().toString(); String leaseToken=UUID.randomUUID().toString(); long leaseNow=now();
            int round=contact.getCurrentRound()+1;
            if(tasks.leaseContact(contact.getId(),taskId,attemptId,leaseNow+claim.getLeaseSeconds(),leaseNow)!=1) continue;
            TaskAttempt attempt=new TaskAttempt(); attempt.setAttemptId(attemptId); attempt.setLeaseToken(leaseToken); attempt.setLeaseUntil(leaseNow+claim.getLeaseSeconds()); attempt.setCompanyId(company); attempt.setTaskId(taskId); attempt.setContactId(contact.getId()); attempt.setRequestId(claim.getRequestId()); attempt.setRound(round); attempt.setState("claimed"); attempt.setCts(leaseNow); attempt.setUts(leaseNow); tasks.insertAttempt(attempt);
            contact.setCurrentRound(round); contact.setActiveAttemptId(attemptId); contact.setCallStatus("dialing"); claimed.add(claimResult(taskId,attempt,contact));
        }
        return claimed;
    }
    private com.voxai.api.vo.task.TaskClaimResult claimResult(Long taskId,TaskAttempt attempt,TaskContact contact) {
        com.voxai.api.vo.task.TaskClaimResult result=new com.voxai.api.vo.task.TaskClaimResult();
        result.setTaskId(taskId); result.setContactId(contact.getId()); result.setAttemptId(attempt.getAttemptId()); result.setRequestId(attempt.getRequestId()); result.setLeaseToken(attempt.getLeaseToken()); result.setLeaseUntil(attempt.getLeaseUntil()); result.setRound(attempt.getRound()); result.setName(contact.getName()); result.setPhone(contact.getPhone()); result.setCustomData(contact.getCustomData()); return result;
    }
    public void heartbeat(Long company,Long taskId,TaskClaim claim) {
        task(company,taskId); require(claim!=null && claim.getAttemptId()!=null && claim.getLeaseToken()!=null,"租约信息缺失");
        range(claim.getLeaseSeconds(),10,300,"租约秒数");
        long current = now();
        require(tasks.renewAttempt(company,taskId,claim.getAttemptId(),claim.getLeaseToken(),current+claim.getLeaseSeconds(),current)==1,"attempt不存在、已过期或租约token不匹配");
    }
    public void completeAttempt(Long company,Long taskId,TaskClaim claim) {
        TaskConfig task=task(company,taskId);
        require(claim!=null && claim.getAttemptId()!=null && claim.getLeaseToken()!=null && claim.getContactId()!=null,"回写关联字段缺失");
        require(claim.getResult()!=null && claim.getResult().matches("answered|no_answer|busy|rejected|invalid|failed|completed"),"呼叫结果值无效");
        require(claim.getCallId()!=null && claim.getCallId()>0,"callId缺失");
        require(claim.getRequestId()!=null && claim.getRequestId().matches("[A-Za-z0-9_-]{16,64}"),"requestId格式错误");
        long current=now();
        TaskAttempt attempt=tasks.attemptByRequest(company,claim.getRequestId());
        if(attempt==null) { throw invalid("拨打记录不存在"); }
        require(Objects.equals(attempt.getTaskId(),taskId) && Objects.equals(attempt.getContactId(),claim.getContactId()) && Objects.equals(attempt.getAttemptId(),claim.getAttemptId()),"拨打关联不匹配");
        require(Objects.equals(attempt.getLeaseToken(),claim.getLeaseToken()), "租约token不匹配");
        if ("completed".equals(attempt.getState())) {
            require(Objects.equals(attempt.getCallId(), claim.getCallId()) && Objects.equals(attempt.getResult(), claim.getResult()), "attempt已用不同结果完成");
            return;
        }
        require("claimed".equals(attempt.getState()), "attempt已过期或不能回写");
        require(tasks.countTaskCall(company, taskId, claim.getCallId())==1, "话单尚未落库或不属于当前企业任务，请续租后重试");
        require(tasks.countCallAttempts(company, taskId, claim.getCallId())==0, "话单已关联其他拨打记录");
        boolean retryable=Arrays.asList("no_answer","busy","failed").contains(claim.getResult());
        boolean retry=retryable && attempt.getRound()<task.getCallRounds();
        String state=retry?"retry":"completed";
        attempt.setCallId(claim.getCallId()); attempt.setState("completed"); attempt.setResult(claim.getResult()); attempt.setUts(current);
        require(tasks.completeAttempt(attempt)==1,"拨打记录已处理或租约已过期");
        TaskContact contact=new TaskContact(); contact.setId(claim.getContactId()); contact.setTaskId(taskId); contact.setActiveAttemptId(claim.getAttemptId());
        contact.setCallTime(current); contact.setAnswerTime(claim.getAnswerTime()); contact.setBridgeTime(claim.getBridgeTime()); contact.setCallStatus(state); contact.setCallResult(claim.getResult()); contact.setAgentCode(claim.getAgentCode());
        contact.setNextAttemptAt(retry?current+task.getRetryInterval():Long.MAX_VALUE);
        require(tasks.completeContact(contact)==1,"名单已被新拨打轮次接管，拒绝覆盖");
        require(tasks.updateTaskCounters(company,taskId,current)==1,"任务统计更新失败");
    }
    private static long now() { return Instant.now().getEpochSecond(); }
    public static void require(boolean condition,String message) { if(!condition) { throw invalid(message); } }
    private static BusinessException invalid(String message) { return new BusinessException(ErrorCode.PARAMETER_ERROR,message); }
    private static void range(Integer value,int min,int max,String name) { require(value!=null && value>=min && value<=max,name+"超出范围"); }
    private static void url(String value) {
        if(value==null || value.isEmpty()) { return; }
        try { java.net.URI uri=java.net.URI.create(value); require(value.length()<=64 && ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme())) && uri.getHost()!=null && uri.getUserInfo()==null, "通知URL无效"); } catch(IllegalArgumentException e) { throw invalid("通知URL无效"); }
    }
}
