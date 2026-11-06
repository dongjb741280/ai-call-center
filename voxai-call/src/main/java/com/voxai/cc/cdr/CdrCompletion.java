package com.voxai.cc.cdr;

import com.voxai.core.entity.CallDetail;
import com.voxai.core.entity.CallDevice;
import com.voxai.core.entity.CallLog;
import java.util.ArrayList;
import java.util.List;

/** One durable MQ message contains everything required to finish a call. */
public class CdrCompletion {
    private CallLog callLog;
    private List<CallDevice> devices = new ArrayList<>();
    private List<CallDetail> details = new ArrayList<>();
    private String notifyUrl;
    private int notifyVersion = 1;

    public CallLog getCallLog() { return callLog; }
    public void setCallLog(CallLog callLog) { this.callLog = callLog; }
    public List<CallDevice> getDevices() { return devices; }
    public void setDevices(List<CallDevice> devices) { this.devices = devices == null ? new ArrayList<>() : devices; }
    public List<CallDetail> getDetails() { return details; }
    public void setDetails(List<CallDetail> details) { this.details = details == null ? new ArrayList<>() : details; }
    public String getNotifyUrl() { return notifyUrl; }
    public void setNotifyUrl(String notifyUrl) { this.notifyUrl = notifyUrl; }
    public int getNotifyVersion() { return notifyVersion; }
    public void setNotifyVersion(int notifyVersion) { this.notifyVersion = notifyVersion; }
}
