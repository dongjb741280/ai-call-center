package com.voxai.core.mapper;

import com.voxai.core.entity.Company;
import com.voxai.core.mapper.base.BaseMapper;
import com.voxai.core.po.CompanyInfo;

import java.util.List;
import java.util.Map;

public interface CompanyMapper extends BaseMapper<Company> {


    /**
     * 逻辑删除
     *
     * @param company
     * @return
     */
    int deleteCompany(Company company);


    /**
     *
     * @return
     */
    List<CompanyInfo> selectCompanyInfoList(Map<String, Object> params);

    CompanyInfo selectById(Long id);
}