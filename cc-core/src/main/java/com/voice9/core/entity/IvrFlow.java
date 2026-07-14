package com.voice9.core.entity;

import java.io.Serializable;

public class IvrFlow implements Serializable {
    private Long id;
    private Long cts;
    private Long callId;
    private Long ivrId;
    private String nodeId;
    private String nodeType;
    private String nodeName;
    private String keyPress;
    private Long startTime;
    private Long endTime;
    private static final long serialVersionUID = 1L;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCts() { return cts; }
    public void setCts(Long cts) { this.cts = cts; }
    public Long getCallId() { return callId; }
    public void setCallId(Long callId) { this.callId = callId; }
    public Long getIvrId() { return ivrId; }
    public void setIvrId(Long ivrId) { this.ivrId = ivrId; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getNodeType() { return nodeType; }
    public void setNodeType(String nodeType) { this.nodeType = nodeType; }
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    public String getKeyPress() { return keyPress; }
    public void setKeyPress(String keyPress) { this.keyPress = keyPress; }
    public Long getStartTime() { return startTime; }
    public void setStartTime(Long startTime) { this.startTime = startTime; }
    public Long getEndTime() { return endTime; }
    public void setEndTime(Long endTime) { this.endTime = endTime; }
}
