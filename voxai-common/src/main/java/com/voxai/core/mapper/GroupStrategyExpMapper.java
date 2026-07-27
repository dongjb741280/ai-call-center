/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.GroupStrategyExp;
import com.voxai.core.mapper.base.BaseMapper;

import java.util.List;

public interface GroupStrategyExpMapper extends BaseMapper<GroupStrategyExp> {

    /**
     *
     * @param groupId
     * @return
     */
    List<GroupStrategyExp> selectList(Long groupId);
}