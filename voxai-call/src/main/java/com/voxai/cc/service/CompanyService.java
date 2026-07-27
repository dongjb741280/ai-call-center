package com.voxai.cc.service;

import com.voxai.core.entity.Company;
import com.voxai.core.po.CompanyInfo;

import java.util.Map;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public interface CompanyService extends BaseService<Company> {

    Map<Long, CompanyInfo> initAll();

    /**
     * 初始化vdn
     *
     * @param companyInfo
     */
    void initVdn(CompanyInfo companyInfo);

}
