package com.voxai.core.mapper;

import com.voxai.core.entity.OverflowFront;
import com.voxai.core.mapper.base.BaseMapper;

import java.util.List;

public interface OverflowFrontMapper extends BaseMapper<OverflowFront> {

    /**
     *
     * @param overflowId
     * @return
     */
    List<OverflowFront> selectByOverflowId(Long overflowId);

}