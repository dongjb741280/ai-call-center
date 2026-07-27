package com.voxai.core.enums;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public enum StationType {

    CC_API(1),

    FS_API(2),

    CC_IVR(3),

    FS_MEDIA(4);

    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    StationType(Integer type){
        this.type = type;
    }
}
