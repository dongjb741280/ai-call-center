package com.voxai.core.po.dialer;

public class DialerStatus  {
    private Long callId;
    public Long getCallId() { return callId; }
    public void setCallId(Long value) { this.callId = value; }
    private boolean completed;
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean value) { this.completed = value; }
    private String result;
    public String getResult() { return result; }
    public void setResult(String value) { this.result = value; }
    private Long answerTime;
    public Long getAnswerTime() { return answerTime; }
    public void setAnswerTime(Long value) { this.answerTime = value; }
    private Long bridgeTime;
    public Long getBridgeTime() { return bridgeTime; }
    public void setBridgeTime(Long value) { this.bridgeTime = value; }
    private String agentCode;
    public String getAgentCode() { return agentCode; }
    public void setAgentCode(String value) { this.agentCode = value; }
}
