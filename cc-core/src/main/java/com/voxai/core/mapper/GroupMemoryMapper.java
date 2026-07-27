package com.voxai.core.mapper;

import com.voxai.core.entity.GroupMemory;
import com.voxai.core.mapper.base.BaseMapper;

public interface GroupMemoryMapper extends BaseMapper<GroupMemory> {

    /**
     * 查询指定时间内的电话记录
     *
     * @param groupMemory
     * @return
     */
    GroupMemory selectByGroup(GroupMemory groupMemory);

}