package com.voxai.core.mapper;

import com.voxai.core.entity.RouteGroup;
import com.voxai.core.mapper.base.BaseMapper;
import com.voxai.core.po.RouteGroupPo;

public interface RouteGroupMapper extends BaseMapper<RouteGroup> {


    /**
     *
     * @param id
     * @return
     */
    RouteGroupPo selectById(Long id);

    /**
     *
     * @param getwayId
     * @return
     */
    RouteGroupPo selectByGetwayId(Long getwayId);
}