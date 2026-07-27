/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.OverflowExp;
import com.voxai.core.mapper.base.BaseMapper;

import java.util.List;

public interface OverflowExpMapper extends BaseMapper<OverflowExp> {

    /**
     * @param overflowId
     * @return
     */
    List<OverflowExp> selectByOverflowId(Long overflowId);
}