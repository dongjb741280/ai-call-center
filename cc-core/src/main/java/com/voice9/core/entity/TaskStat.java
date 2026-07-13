package com.voice9.core.entity;

import java.io.Serializable;

public class TaskStat implements Serializable {
    private Long taskId;
    private String taskName;
    private String transferName;
    private Long statsTime;
    private Integer status;
    private Integer listTotal;
    private Integer dialedCount;
    private Integer answeredCount;
    private String answerRate;
    private Integer pendingCount;
    private Integer inCall;
    private Integer queueCount;
    private Integer signedInAgents;
    private Integer idleAgents;
    private Integer busyAgents;
    private Integer afterCallAgents;
    private Integer abandonedCount;
    private static final long serialVersionUID = 1L;

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getTransferName() { return transferName; }
    public void setTransferName(String transferName) { this.transferName = transferName; }
    public Long getStatsTime() { return statsTime; }
    public void setStatsTime(Long statsTime) { this.statsTime = statsTime; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getListTotal() { return listTotal; }
    public void setListTotal(Integer listTotal) { this.listTotal = listTotal; }
    public Integer getDialedCount() { return dialedCount; }
    public void setDialedCount(Integer dialedCount) { this.dialedCount = dialedCount; }
    public Integer getAnsweredCount() { return answeredCount; }
    public void setAnsweredCount(Integer answeredCount) { this.answeredCount = answeredCount; }
    public String getAnswerRate() { return answerRate; }
    public void setAnswerRate(String answerRate) { this.answerRate = answerRate; }
    public Integer getPendingCount() { return pendingCount; }
    public void setPendingCount(Integer pendingCount) { this.pendingCount = pendingCount; }
    public Integer getInCall() { return inCall; }
    public void setInCall(Integer inCall) { this.inCall = inCall; }
    public Integer getQueueCount() { return queueCount; }
    public void setQueueCount(Integer queueCount) { this.queueCount = queueCount; }
    public Integer getSignedInAgents() { return signedInAgents; }
    public void setSignedInAgents(Integer signedInAgents) { this.signedInAgents = signedInAgents; }
    public Integer getIdleAgents() { return idleAgents; }
    public void setIdleAgents(Integer idleAgents) { this.idleAgents = idleAgents; }
    public Integer getBusyAgents() { return busyAgents; }
    public void setBusyAgents(Integer busyAgents) { this.busyAgents = busyAgents; }
    public Integer getAfterCallAgents() { return afterCallAgents; }
    public void setAfterCallAgents(Integer afterCallAgents) { this.afterCallAgents = afterCallAgents; }
    public Integer getAbandonedCount() { return abandonedCount; }
    public void setAbandonedCount(Integer abandonedCount) { this.abandonedCount = abandonedCount; }
}
