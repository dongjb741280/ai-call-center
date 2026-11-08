package com.voxai.core.po.dialer;

public class DialerRequest  {
    private String attemptId;
    public String getAttemptId() { return attemptId; }
    public void setAttemptId(String value) { this.attemptId = value; }
    private String leaseToken;
    public String getLeaseToken() { return leaseToken; }
    public void setLeaseToken(String value) { this.leaseToken = value; }
}
