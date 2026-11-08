package com.voxai.core.po.dialer;

public class DialerCapacity  {
    private int readyAgents;
    public int getReadyAgents() { return readyAgents; }
    public void setReadyAgents(int value) { this.readyAgents = value; }
    private int activeCalls;
    public int getActiveCalls() { return activeCalls; }
    public void setActiveCalls(int value) { this.activeCalls = value; }
    private int targetActiveCalls;
    public int getTargetActiveCalls() { return targetActiveCalls; }
    public void setTargetActiveCalls(int value) { this.targetActiveCalls = value; }
    private int companyActiveCalls;
    public int getCompanyActiveCalls() { return companyActiveCalls; }
    public void setCompanyActiveCalls(int value) { this.companyActiveCalls = value; }
    private int completedCalls;
    public int getCompletedCalls() { return completedCalls; }
    public void setCompletedCalls(int value) { this.completedCalls = value; }
    private int answeredCalls;
    public int getAnsweredCalls() { return answeredCalls; }
    public void setAnsweredCalls(int value) { this.answeredCalls = value; }
    private int abandonedCalls;
    public int getAbandonedCalls() { return abandonedCalls; }
    public void setAbandonedCalls(int value) { this.abandonedCalls = value; }
    private int allowance;
    public int getAllowance() { return allowance; }
    public void setAllowance(int value) { this.allowance = value; }
}
