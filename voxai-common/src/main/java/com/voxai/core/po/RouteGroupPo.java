/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.po;

import com.voxai.core.entity.RouteGetway;
import com.voxai.core.entity.RouteGroup;

import java.util.List;

/**
 * 路由网关组
 */
public class RouteGroupPo  extends RouteGroup {

    /**
     * 网关列表
     */
    private List<RouteGetway> routeGetways;

    public List<RouteGetway> getRouteGetways() {
        return routeGetways;
    }

    public void setRouteGetways(List<RouteGetway> routeGetways) {
        this.routeGetways = routeGetways;
    }
}
