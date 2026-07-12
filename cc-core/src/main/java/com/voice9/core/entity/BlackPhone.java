package com.voice9.core.entity;

import java.io.Serializable;

public class BlackPhone implements Serializable {
    private Long id;
    private Long cts;
    private Long uts;
    private Long companyId;
    private String numPrefix;
    private Integer type;
    private String thridUrl;
    private String thridMethod;
    private String requestBody;
    private String responseBody;
    private Integer callDirection;
    private Long scheduleId;
    private String scheduleName;
    private Integer status;

    private static final long serialVersionUID = 1L;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCts() { return cts; }
    public void setCts(Long cts) { this.cts = cts; }
    public Long getUts() { return uts; }
    public void setUts(Long uts) { this.uts = uts; }
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    public String getNumPrefix() { return numPrefix; }
    public void setNumPrefix(String numPrefix) { this.numPrefix = numPrefix; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public String getThridUrl() { return thridUrl; }
    public void setThridUrl(String thridUrl) { this.thridUrl = thridUrl; }
    public String getThridMethod() { return thridMethod; }
    public void setThridMethod(String thridMethod) { this.thridMethod = thridMethod; }
    public String getRequestBody() { return requestBody; }
    public void setRequestBody(String requestBody) { this.requestBody = requestBody; }
    public String getResponseBody() { return responseBody; }
    public void setResponseBody(String responseBody) { this.responseBody = responseBody; }
    public Integer getCallDirection() { return callDirection; }
    public void setCallDirection(Integer callDirection) { this.callDirection = callDirection; }
    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public String getScheduleName() { return scheduleName; }
    public void setScheduleName(String scheduleName) { this.scheduleName = scheduleName; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
