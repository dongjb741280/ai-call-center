package com.voxai.core.mapper;

import com.voxai.core.entity.CompanyPhone;
import com.voxai.core.mapper.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CompanyPhoneMapper extends BaseMapper<CompanyPhone> {

    /**
     * 查询
     *
     * @param companyId
     * @param id
     * @return
     */
    @Override
    CompanyPhone selectById(@Param("companyId") Long companyId, @Param("id") Long id);

    /**
     * 通过号码池id查询
     *
     * @param id
     * @return
     */
    List<CompanyPhone> selectByDisplay(Long id);
}