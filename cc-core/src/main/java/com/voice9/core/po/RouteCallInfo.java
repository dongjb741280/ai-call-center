package com.voice9.core.po;

import com.voice9.core.entity.RouteCall;

/**
 * Created by dongjb on 2025/09/03
 */
public class RouteCallInfo extends RouteCall {

    /**
     * 网关组
     */
    private RouteGroupPo routeGroupPo;

    public RouteGroupPo getRouteGroupPo() {
        return routeGroupPo;
    }

    public void setRouteGroupPo(RouteGroupPo routeGroupPo) {
        this.routeGroupPo = routeGroupPo;
    }
}
