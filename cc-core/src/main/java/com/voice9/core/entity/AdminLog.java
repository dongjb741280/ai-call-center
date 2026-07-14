package com.voice9.core.entity;

import java.io.Serializable;

public class AdminLog implements Serializable {
    private Long id;
    private Long cts;
    private Long uts;
    private Long companyId;
    private String account;
    private Long opTime;
    private Long enterpriseId;
    private String requestUrl;
    private Integer duration;
    private String userIp;
    private String region;
    private String isp;
    private String serverAddr;
    private String requestParams;
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
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account == null ? null : account.trim(); }
    public Long getOpTime() { return opTime; }
    public void setOpTime(Long opTime) { this.opTime = opTime; }
    public Long getEnterpriseId() { return enterpriseId; }
    public void setEnterpriseId(Long enterpriseId) { this.enterpriseId = enterpriseId; }
    public String getRequestUrl() { return requestUrl; }
    public void setRequestUrl(String requestUrl) { this.requestUrl = requestUrl == null ? null : requestUrl.trim(); }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public String getUserIp() { return userIp; }
    public void setUserIp(String userIp) { this.userIp = userIp == null ? null : userIp.trim(); }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region == null ? null : region.trim(); }
    public String getIsp() { return isp; }
    public void setIsp(String isp) { this.isp = isp == null ? null : isp.trim(); }
    public String getServerAddr() { return serverAddr; }
    public void setServerAddr(String serverAddr) { this.serverAddr = serverAddr == null ? null : serverAddr.trim(); }
    public String getRequestParams() { return requestParams; }
    public void setRequestParams(String requestParams) { this.requestParams = requestParams == null ? null : requestParams.trim(); }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
