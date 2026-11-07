package com.voxai.api.vo.task;

public class TaskCommand {
    private String action;
    public String getAction() { return action; }
    public void setAction(String value) { this.action = value; }
    private String commandId;
    public String getCommandId() { return commandId; }
    public void setCommandId(String value) { this.commandId = value; }
    private Long version;
    public Long getVersion() { return version; }
    public void setVersion(Long value) { this.version = value; }
    private Boolean success;
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean value) { this.success = value; }
}
