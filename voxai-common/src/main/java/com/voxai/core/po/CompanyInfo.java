package com.voxai.core.po;

import com.voxai.core.entity.Company;
import com.voxai.core.entity.CompanyDisplay;
import com.voxai.core.entity.CompanyStat;

import java.util.List;
import java.util.Map;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class CompanyInfo extends Company {

    /**
     * 企业技能组
     */
    private List<Long> groupIds;


    /**
     * 企业号码池
     */
    private List<CompanyDisplay> companyDisplays;

    /**
     * vdnId - vdnCode
     */
    private Map<Long, VdnCodePo> vdnCodeMap;


    /**
     * 企业路由字冠集合
     */
    private Map<String, RouteGroupPo> routeGroupMap;

    /**
     * 企业报表统计
     */
    private List<CompanyStat> companyStats;



    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    public List<CompanyDisplay> getCompanyDisplays() {
        return companyDisplays;
    }

    public void setCompanyDisplays(List<CompanyDisplay> companyDisplays) {
        this.companyDisplays = companyDisplays;
    }

    public Map<Long, VdnCodePo> getVdnCodeMap() {
        return vdnCodeMap;
    }

    public void setVdnCodeMap(Map<Long, VdnCodePo> vdnCodeMap) {
        this.vdnCodeMap = vdnCodeMap;
    }

    public Map<String, RouteGroupPo> getRouteGroupMap() {
        return routeGroupMap;
    }

    public void setRouteGroupMap(Map<String, RouteGroupPo> routeGroupMap) {
        this.routeGroupMap = routeGroupMap;
    }

    public List<CompanyStat> getCompanyStats() {
        return companyStats;
    }

    public void setCompanyStats(List<CompanyStat> companyStats) {
        this.companyStats = companyStats;
    }

    
}
