/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.CompanyStat;
import com.voxai.core.mapper.base.BaseMapper;

import java.util.List;


public interface CompanyStatMapper extends BaseMapper<CompanyStat> {

    /**
     * 查询企业
     *
     * @param companyId
     * @return
     */
    List<CompanyStat> selectByCompanyId(Long companyId);
}