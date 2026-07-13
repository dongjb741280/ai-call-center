package com.voice9.core.entity;

import java.io.Serializable;

public class TaskConfig implements Serializable {
    private Long id;
    private Long cts;
    private Long uts;
    private Long companyId;
    private String name;
    private String callStartTime;
    private String callEndTime;
    private Long closeTime;
    private String transferType;
    private Integer totalCount;
    private Integer completedCount;
    private Integer answeredCount;
    private Integer status;
    private String displayNumber;
    private Integer callRounds;
    private Integer currentRound;
    private String dataSource;
    private String transferName;
    private Integer dialedCount;
    private String cruiseType;
    private Integer concurrency;
    private String numberPoolName;
    private Integer afterCallDuration;
    private Integer callTimeout;
    private String statusNotify;
    private String trafficNotify;
    private Integer listTotal;
    private Long pauseStartTime;
    private Long pauseEndTime;

    private static final long serialVersionUID = 1L;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCts() { return cts; }
    public void setCts(Long cts) { this.cts = cts; }
    public Long getUts() { return uts; }
    public void setUts(Long uts) { this.uts = uts; }
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? null : name.trim(); }
    public String getCallStartTime() { return callStartTime; }
    public void setCallStartTime(String callStartTime) { this.callStartTime = callStartTime; }
    public String getCallEndTime() { return callEndTime; }
    public void setCallEndTime(String callEndTime) { this.callEndTime = callEndTime; }
    public Long getCloseTime() { return closeTime; }
    public void setCloseTime(Long closeTime) { this.closeTime = closeTime; }
    public String getTransferType() { return transferType; }
    public void setTransferType(String transferType) { this.transferType = transferType; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getCompletedCount() { return completedCount; }
    public void setCompletedCount(Integer completedCount) { this.completedCount = completedCount; }
    public Integer getAnsweredCount() { return answeredCount; }
    public void setAnsweredCount(Integer answeredCount) { this.answeredCount = answeredCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getDisplayNumber() { return displayNumber; }
    public void setDisplayNumber(String displayNumber) { this.displayNumber = displayNumber; }
    public Integer getCallRounds() { return callRounds; }
    public void setCallRounds(Integer callRounds) { this.callRounds = callRounds; }
    public Integer getCurrentRound() { return currentRound; }
    public void setCurrentRound(Integer currentRound) { this.currentRound = currentRound; }
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    public String getTransferName() { return transferName; }
    public void setTransferName(String transferName) { this.transferName = transferName; }
    public Integer getDialedCount() { return dialedCount; }
    public void setDialedCount(Integer dialedCount) { this.dialedCount = dialedCount; }
    public String getCruiseType() { return cruiseType; }
    public void setCruiseType(String cruiseType) { this.cruiseType = cruiseType; }
    public Integer getConcurrency() { return concurrency; }
    public void setConcurrency(Integer concurrency) { this.concurrency = concurrency; }
    public String getNumberPoolName() { return numberPoolName; }
    public void setNumberPoolName(String numberPoolName) { this.numberPoolName = numberPoolName; }
    public Integer getAfterCallDuration() { return afterCallDuration; }
    public void setAfterCallDuration(Integer afterCallDuration) { this.afterCallDuration = afterCallDuration; }
    public Integer getCallTimeout() { return callTimeout; }
    public void setCallTimeout(Integer callTimeout) { this.callTimeout = callTimeout; }
    public String getStatusNotify() { return statusNotify; }
    public void setStatusNotify(String statusNotify) { this.statusNotify = statusNotify; }
    public String getTrafficNotify() { return trafficNotify; }
    public void setTrafficNotify(String trafficNotify) { this.trafficNotify = trafficNotify; }
    public Integer getListTotal() { return listTotal; }
    public void setListTotal(Integer listTotal) { this.listTotal = listTotal; }
    public Long getPauseStartTime() { return pauseStartTime; }
    public void setPauseStartTime(Long pauseStartTime) { this.pauseStartTime = pauseStartTime; }
    public Long getPauseEndTime() { return pauseEndTime; }
    public void setPauseEndTime(Long pauseEndTime) { this.pauseEndTime = pauseEndTime; }
}
