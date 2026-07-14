package com.voice9.core.po;

import java.io.Serializable;

public class AgentMonitorVo implements Serializable {
    private String agentKey;
    private String agentName;
    private Integer loginType;
    private Integer workType;
    private String state;
    private Long stateTime;
    private String host;
    private static final long serialVersionUID = 1L;

    public String getAgentKey() { return agentKey; }
    public void setAgentKey(String v) { this.agentKey = v; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String v) { this.agentName = v; }
    public Integer getLoginType() { return loginType; }
    public void setLoginType(Integer v) { this.loginType = v; }
    public Integer getWorkType() { return workType; }
    public void setWorkType(Integer v) { this.workType = v; }
    public String getState() { return state; }
    public void setState(String v) { this.state = v; }
    public Long getStateTime() { return stateTime; }
    public void setStateTime(Long v) { this.stateTime = v; }
    public String getHost() { return host; }
    public void setHost(String v) { this.host = v; }
}
