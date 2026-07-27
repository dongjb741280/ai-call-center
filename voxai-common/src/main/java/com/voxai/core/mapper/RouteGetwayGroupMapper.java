/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.RouteGetwayGroup;
import com.voxai.core.mapper.base.BaseMapper;

public interface RouteGetwayGroupMapper extends BaseMapper<RouteGetwayGroup> {


    /**
     * 删除数据
     *
     * @param id
     * @return
     */
    int deleteByRoutegroup(Long id);
}