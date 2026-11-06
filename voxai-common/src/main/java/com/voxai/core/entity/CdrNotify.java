package com.voxai.core.entity;

/** Transactional CDR outbox; independent of the legacy hourly cc_push_log retries. */
public class CdrNotify extends PushLog {
    private Long leaseUntil;
    private String leaseToken;
    public Long getLeaseUntil() { return leaseUntil; }
    public void setLeaseUntil(Long value) { leaseUntil = value; }
    public String getLeaseToken() { return leaseToken; }
    public void setLeaseToken(String value) { leaseToken = value; }
}
