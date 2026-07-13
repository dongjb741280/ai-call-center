package com.voice9.core.entity;

import java.io.Serializable;

public class TaskContact implements Serializable {
    private Long id;
    private Long taskId;
    private String name;
    private String phone;
    private Integer currentRound;
    private String uuid;
    private Long callTime;
    private Long answerTime;
    private String callStatus;
    private String callResult;
    private String agentCode;
    private Long bridgeTime;

    private static final long serialVersionUID = 1L;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getCurrentRound() { return currentRound; }
    public void setCurrentRound(Integer currentRound) { this.currentRound = currentRound; }
    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }
    public Long getCallTime() { return callTime; }
    public void setCallTime(Long callTime) { this.callTime = callTime; }
    public Long getAnswerTime() { return answerTime; }
    public void setAnswerTime(Long answerTime) { this.answerTime = answerTime; }
    public String getCallStatus() { return callStatus; }
    public void setCallStatus(String callStatus) { this.callStatus = callStatus; }
    public String getCallResult() { return callResult; }
    public void setCallResult(String callResult) { this.callResult = callResult; }
    public String getAgentCode() { return agentCode; }
    public void setAgentCode(String agentCode) { this.agentCode = agentCode; }
    public Long getBridgeTime() { return bridgeTime; }
    public void setBridgeTime(Long bridgeTime) { this.bridgeTime = bridgeTime; }
}
