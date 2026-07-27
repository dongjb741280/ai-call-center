package com.voxai.core.po;

import com.voxai.core.entity.RouteCall;

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
