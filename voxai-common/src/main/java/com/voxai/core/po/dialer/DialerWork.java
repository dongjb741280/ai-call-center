package com.voxai.core.po.dialer;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown=true)
public class DialerWork extends com.voxai.core.entity.TaskAttempt {
    private String phone;
    public String getPhone() { return phone; }
    public void setPhone(String value) { this.phone = value; }
    private String customData;
    public String getCustomData() { return customData; }
    public void setCustomData(String value) { this.customData = value; }
}
