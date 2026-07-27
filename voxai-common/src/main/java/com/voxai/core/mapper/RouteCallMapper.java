package com.voxai.core.mapper;

import com.voxai.core.entity.RouteCall;
import com.voxai.core.mapper.base.BaseMapper;
import com.voxai.core.po.RouteCallInfo;

import java.util.List;
import java.util.Map;

public interface RouteCallMapper extends BaseMapper<RouteCallInfo> {

    /**
     * 查询分页
     *
     * @param params
     * @return
     */
    List<RouteCall> selectList(Map<String, Object> params);


    /**
     * 新增
     *
     * @param routeCall
     * @return
     */
    int insertSelective(RouteCall routeCall);

    /**
     * 修改
     *
     * @param routeCall
     * @return
     */
    int updateByPrimaryKeySelective(RouteCall routeCall);
}