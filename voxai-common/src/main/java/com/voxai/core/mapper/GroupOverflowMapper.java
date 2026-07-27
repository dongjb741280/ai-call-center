/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.GroupOverflow;
import com.voxai.core.mapper.base.BaseMapper;
import com.voxai.core.po.GroupOverflowPo;

import java.util.List;

public interface GroupOverflowMapper extends BaseMapper<GroupOverflow> {


    /**
     *
     * @param groupId
     * @return
     */
    List<GroupOverflowPo> selectByOverflow(Long groupId);
}