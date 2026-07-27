/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.CompanyDisplay;
import com.voxai.core.mapper.base.BaseMapper;
import com.voxai.core.po.CompanyDisplayPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CompanyDisplayMapper extends BaseMapper<CompanyDisplay> {

    /**
     * 分页
     *
     * @param params
     * @return
     */
    List<CompanyDisplayPo> selectPage(Map<String, Object> params);

    /**
     * 查询
     *
     * @param companyId
     * @param id
     * @return
     */
    @Override
    CompanyDisplay selectById(@Param("companyId") Long companyId, @Param("id") Long id);
}