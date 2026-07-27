package com.voxai.core.enums;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public enum Direction {
    /**
     * 呼入
     */
    INBOUND(1),

    /**
     * 外呼
     */
    OUTBOUND(2);

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    Direction(Integer code) {
        this.code = code;
    }
}
