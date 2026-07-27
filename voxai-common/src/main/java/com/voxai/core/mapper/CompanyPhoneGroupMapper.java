/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.CompanyPhoneGroup;
import com.voxai.core.mapper.base.BaseMapper;

import java.util.List;

public interface CompanyPhoneGroupMapper extends BaseMapper<CompanyPhoneGroup> {

    /**
     * 通过号码池id查询号码
     *
     * @param displayId
     * @return
     */
    List<String> selectDisplayByGroupId(Long displayId);

}