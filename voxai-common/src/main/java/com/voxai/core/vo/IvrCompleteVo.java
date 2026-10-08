package com.voxai.core.vo;

/**
 * IVR 流程结束回调请求体（voxai-ivr → voxai-call）
 */
public class IvrCompleteVo {

    /**
     * 通话唯一标识
     */
    private Long callId;

    /**
     * 设备 ID（被转 IVR 的 FreeSwitch channel UUID）
     */
    private String deviceId;

    /**
     * 转接目标类型：group / vdn / agent / hangup
     */
    private String transferType;

    /**
     * 转接目标值（技能组 id / vdn id / 坐席 agentKey / 空）
     */
    private String transferValue;

    public Long getCallId() {
        return callId;
    }

    public void setCallId(Long callId) {
        this.callId = callId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getTransferValue() {
        return transferValue;
    }

    public void setTransferValue(String transferValue) {
        this.transferValue = transferValue;
    }
}
