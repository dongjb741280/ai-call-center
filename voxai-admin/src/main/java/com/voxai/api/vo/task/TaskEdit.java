package com.voxai.api.vo.task;

public class TaskEdit {
    private String name;
    public String getName() { return name; }
    public void setName(String value) { this.name = value; }
    private String callStartTime;
    public String getCallStartTime() { return callStartTime; }
    public void setCallStartTime(String value) { this.callStartTime = value; }
    private String callEndTime;
    public String getCallEndTime() { return callEndTime; }
    public void setCallEndTime(String value) { this.callEndTime = value; }
    private Long closeTime;
    public Long getCloseTime() { return closeTime; }
    public void setCloseTime(Long value) { this.closeTime = value; }
    private Long sourceId;
    public Long getSourceId() { return sourceId; }
    public void setSourceId(Long value) { this.sourceId = value; }
    private String transferType;
    public String getTransferType() { return transferType; }
    public void setTransferType(String value) { this.transferType = value; }
    private Long transferId;
    public Long getTransferId() { return transferId; }
    public void setTransferId(Long value) { this.transferId = value; }
    private String displayNumber;
    public String getDisplayNumber() { return displayNumber; }
    public void setDisplayNumber(String value) { this.displayNumber = value; }
    private Integer callRounds;
    public Integer getCallRounds() { return callRounds; }
    public void setCallRounds(Integer value) { this.callRounds = value; }
    private String cruiseType;
    public String getCruiseType() { return cruiseType; }
    public void setCruiseType(String value) { this.cruiseType = value; }
    private Integer concurrency;
    public Integer getConcurrency() { return concurrency; }
    public void setConcurrency(Integer value) { this.concurrency = value; }
    private Long numberPoolId;
    public Long getNumberPoolId() { return numberPoolId; }
    public void setNumberPoolId(Long value) { this.numberPoolId = value; }
    private Integer afterCallDuration;
    public Integer getAfterCallDuration() { return afterCallDuration; }
    public void setAfterCallDuration(Integer value) { this.afterCallDuration = value; }
    private Integer callTimeout;
    public Integer getCallTimeout() { return callTimeout; }
    public void setCallTimeout(Integer value) { this.callTimeout = value; }
    private String statusNotify;
    public String getStatusNotify() { return statusNotify; }
    public void setStatusNotify(String value) { this.statusNotify = value; }
    private String trafficNotify;
    public String getTrafficNotify() { return trafficNotify; }
    public void setTrafficNotify(String value) { this.trafficNotify = value; }
    private Integer retryInterval;
    public Integer getRetryInterval() { return retryInterval; }
    public void setRetryInterval(Integer value) { this.retryInterval = value; }
    private Long version;
    public Long getVersion() { return version; }
    public void setVersion(Long value) { this.version = value; }
}
