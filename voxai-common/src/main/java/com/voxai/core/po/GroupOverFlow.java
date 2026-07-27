package com.voxai.core.po;

import com.voxai.core.entity.OverflowConfig;
import com.voxai.core.entity.OverflowExp;
import com.voxai.core.entity.OverflowFront;

import java.util.List;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class GroupOverFlow extends OverflowConfig {


    /**
     * 前置条件
     */
    protected List<OverflowFront> overflowFronts;


    /**
     * 自定义策略
     */
    private List<OverflowExp> overflowExps;

    public String getCalculateExp(){
        return "";
    }

    public List<OverflowFront> getOverflowFronts() {
        return overflowFronts;
    }

    public void setOverflowFronts(List<OverflowFront> overflowFronts) {
        this.overflowFronts = overflowFronts;
    }

    public List<OverflowExp> getOverflowExps() {
        return overflowExps;
    }

    public void setOverflowExps(List<OverflowExp> overflowExps) {
        this.overflowExps = overflowExps;
    }
}
