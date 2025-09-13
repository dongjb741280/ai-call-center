package com.voice9.core.po;

import com.voice9.core.entity.OverflowConfig;
import com.voice9.core.entity.OverflowExp;
import com.voice9.core.entity.OverflowFront;

import java.util.List;

/**
 * Created by dongjb on 2025/09/03
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
