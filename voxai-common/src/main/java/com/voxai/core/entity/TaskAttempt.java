package com.voxai.core.entity;

public class TaskAttempt {
    private String mediaHost;
    public String getMediaHost() { return mediaHost; }
    public void setMediaHost(String value) { mediaHost = value; }
    private Long dispatchTime;
    public Long getDispatchTime() { return dispatchTime; }
    public void setDispatchTime(Long value) { dispatchTime = value; }
    private String attemptId;
    public String getAttemptId() { return attemptId; }
    public void setAttemptId(String value) { this.attemptId = value; }
    private Long companyId;
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long value) { this.companyId = value; }
    private Long taskId;
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long value) { this.taskId = value; }
    private Long contactId;
    public Long getContactId() { return contactId; }
    public void setContactId(Long value) { this.contactId = value; }
    private String requestId;
    public String getRequestId() { return requestId; }
    public void setRequestId(String value) { this.requestId = value; }
    private String leaseToken;
    public String getLeaseToken() { return leaseToken; }
    public void setLeaseToken(String value) { this.leaseToken = value; }
    private Long leaseUntil;
    public Long getLeaseUntil() { return leaseUntil; }
    public void setLeaseUntil(Long value) { this.leaseUntil = value; }
    private Long callId;
    public Long getCallId() { return callId; }
    public void setCallId(Long value) { this.callId = value; }
    private Integer round;
    public Integer getRound() { return round; }
    public void setRound(Integer value) { this.round = value; }
    private String state;
    public String getState() { return state; }
    public void setState(String value) { this.state = value; }
    private String result;
    public String getResult() { return result; }
    public void setResult(String value) { this.result = value; }
    private Long cts;
    public Long getCts() { return cts; }
    public void setCts(Long value) { this.cts = value; }
    private Long uts;
    public Long getUts() { return uts; }
    public void setUts(Long value) { this.uts = value; }
}
