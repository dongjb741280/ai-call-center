package com.voxai.api.vo.task;

public class TaskClaimResult {
    private Long taskId;
    private Long contactId;
    private String attemptId;
    private String requestId;
    private String leaseToken;
    private Long leaseUntil;
    private Integer round;
    private String name;
    private String phone;
    private String customData;
    public Long getTaskId(){return taskId;} public void setTaskId(Long v){taskId=v;}
    public Long getContactId(){return contactId;} public void setContactId(Long v){contactId=v;}
    public String getAttemptId(){return attemptId;} public void setAttemptId(String v){attemptId=v;}
    public String getRequestId(){return requestId;} public void setRequestId(String v){requestId=v;}
    public String getLeaseToken(){return leaseToken;} public void setLeaseToken(String v){leaseToken=v;}
    public Long getLeaseUntil(){return leaseUntil;} public void setLeaseUntil(Long v){leaseUntil=v;}
    public Integer getRound(){return round;} public void setRound(Integer v){round=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getCustomData(){return customData;} public void setCustomData(String v){customData=v;}
}
