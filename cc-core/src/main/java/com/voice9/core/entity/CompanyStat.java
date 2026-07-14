package com.voice9.core.entity;

import java.io.Serializable;

public class CompanyStat implements Serializable {
    private Long id;
    private Long cts;
    private Long uts;
    private Long companyId;
    private Integer type;
    private Integer status;
    private Long statTime;
    private Integer agentTotal;
    private Integer agentUsed;
    private Integer callTotal;
    private Integer planTotal;
    private Integer outboundTotal;
    private Integer outboundAnswered;
    private Integer taskOutbound;
    private Integer agentOutboundAnswered;
    private Integer inboundTotal;
    private Integer inboundAssigned;
    private Integer inboundAnswered;
    private Integer ivrCalls;
    private Integer abandonedCnt;
    private Integer recordingCnt;
    private Long recordingSize;
    private Long callDuration;
    private Long inboundDuration;
    private Long outboundDuration;
    private Integer queueCnt;
    private Long queueDuration;
    private Integer loginCnt;
    private Long loginDuration;
    private Integer idleCnt;
    private Long idleDuration;
    private Integer busyCnt;
    private Long busyDuration;
    private Integer afterCallCnt;
    private Long afterCallDuration;
    private Long customBusyDuration;
    private static final long serialVersionUID = 1L;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCts() { return cts; }
    public void setCts(Long cts) { this.cts = cts; }
    public Long getUts() { return uts; }
    public void setUts(Long uts) { this.uts = uts; }
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Long getStatTime() { return statTime; }
    public void setStatTime(Long v) { this.statTime = v; }
    public Integer getAgentTotal() { return agentTotal; }
    public void setAgentTotal(Integer v) { this.agentTotal = v; }
    public Integer getAgentUsed() { return agentUsed; }
    public void setAgentUsed(Integer v) { this.agentUsed = v; }
    public Integer getCallTotal() { return callTotal; }
    public void setCallTotal(Integer v) { this.callTotal = v; }
    public Integer getPlanTotal() { return planTotal; }
    public void setPlanTotal(Integer v) { this.planTotal = v; }
    public Integer getOutboundTotal() { return outboundTotal; }
    public void setOutboundTotal(Integer v) { this.outboundTotal = v; }
    public Integer getOutboundAnswered() { return outboundAnswered; }
    public void setOutboundAnswered(Integer v) { this.outboundAnswered = v; }
    public Integer getTaskOutbound() { return taskOutbound; }
    public void setTaskOutbound(Integer v) { this.taskOutbound = v; }
    public Integer getAgentOutboundAnswered() { return agentOutboundAnswered; }
    public void setAgentOutboundAnswered(Integer v) { this.agentOutboundAnswered = v; }
    public Integer getInboundTotal() { return inboundTotal; }
    public void setInboundTotal(Integer v) { this.inboundTotal = v; }
    public Integer getInboundAssigned() { return inboundAssigned; }
    public void setInboundAssigned(Integer v) { this.inboundAssigned = v; }
    public Integer getInboundAnswered() { return inboundAnswered; }
    public void setInboundAnswered(Integer v) { this.inboundAnswered = v; }
    public Integer getIvrCalls() { return ivrCalls; }
    public void setIvrCalls(Integer v) { this.ivrCalls = v; }
    public Integer getAbandonedCnt() { return abandonedCnt; }
    public void setAbandonedCnt(Integer v) { this.abandonedCnt = v; }
    public Integer getRecordingCnt() { return recordingCnt; }
    public void setRecordingCnt(Integer v) { this.recordingCnt = v; }
    public Long getRecordingSize() { return recordingSize; }
    public void setRecordingSize(Long v) { this.recordingSize = v; }
    public Long getCallDuration() { return callDuration; }
    public void setCallDuration(Long v) { this.callDuration = v; }
    public Long getInboundDuration() { return inboundDuration; }
    public void setInboundDuration(Long v) { this.inboundDuration = v; }
    public Long getOutboundDuration() { return outboundDuration; }
    public void setOutboundDuration(Long v) { this.outboundDuration = v; }
    public Integer getQueueCnt() { return queueCnt; }
    public void setQueueCnt(Integer v) { this.queueCnt = v; }
    public Long getQueueDuration() { return queueDuration; }
    public void setQueueDuration(Long v) { this.queueDuration = v; }
    public Integer getLoginCnt() { return loginCnt; }
    public void setLoginCnt(Integer v) { this.loginCnt = v; }
    public Long getLoginDuration() { return loginDuration; }
    public void setLoginDuration(Long v) { this.loginDuration = v; }
    public Integer getIdleCnt() { return idleCnt; }
    public void setIdleCnt(Integer v) { this.idleCnt = v; }
    public Long getIdleDuration() { return idleDuration; }
    public void setIdleDuration(Long v) { this.idleDuration = v; }
    public Integer getBusyCnt() { return busyCnt; }
    public void setBusyCnt(Integer v) { this.busyCnt = v; }
    public Long getBusyDuration() { return busyDuration; }
    public void setBusyDuration(Long v) { this.busyDuration = v; }
    public Integer getAfterCallCnt() { return afterCallCnt; }
    public void setAfterCallCnt(Integer v) { this.afterCallCnt = v; }
    public Long getAfterCallDuration() { return afterCallDuration; }
    public void setAfterCallDuration(Long v) { this.afterCallDuration = v; }
    public Long getCustomBusyDuration() { return customBusyDuration; }
    public void setCustomBusyDuration(Long v) { this.customBusyDuration = v; }
}
