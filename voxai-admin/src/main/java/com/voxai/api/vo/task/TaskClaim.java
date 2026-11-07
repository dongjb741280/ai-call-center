package com.voxai.api.vo.task;

public class TaskClaim {
    private String requestId;
    private String leaseToken;
    private Integer leaseSeconds;
    private Integer limit;
    private Long contactId;
    private String attemptId;
    private String result;
    private Long callId;
    private Long answerTime;
    private Long bridgeTime;
    private String agentCode;
    private Boolean answered;
    public String getRequestId() { return requestId; }
    public void setRequestId(String value) { this.requestId = value; }
    public String getLeaseToken() { return leaseToken; }
    public void setLeaseToken(String value) { this.leaseToken = value; }
    public Integer getLeaseSeconds() { return leaseSeconds; }
    public void setLeaseSeconds(Integer value) { this.leaseSeconds = value; }
    public Integer getLimit() { return limit; }
    public void setLimit(Integer value) { this.limit = value; }
    public Long getContactId() { return contactId; }
    public void setContactId(Long value) { this.contactId = value; }
    public String getAttemptId() { return attemptId; }
    public void setAttemptId(String value) { this.attemptId = value; }
    public String getResult() { return result; }
    public void setResult(String value) { this.result = value; }
    public Long getCallId() { return callId; }
    public void setCallId(Long value) { this.callId = value; }
    public Long getAnswerTime() { return answerTime; }
    public void setAnswerTime(Long value) { this.answerTime = value; }
    public Long getBridgeTime() { return bridgeTime; }
    public void setBridgeTime(Long value) { this.bridgeTime = value; }
    public String getAgentCode() { return agentCode; }
    public void setAgentCode(String value) { this.agentCode = value; }
    public Boolean getAnswered() { return answered; }
    public void setAnswered(Boolean value) { this.answered = value; }
}
