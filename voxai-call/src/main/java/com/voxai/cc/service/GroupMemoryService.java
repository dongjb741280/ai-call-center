package com.voxai.cc.service;

import com.voxai.core.entity.GroupMemory;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public interface GroupMemoryService extends BaseService<GroupMemory>{

    /**
     * 查询指定时间内的电话记录
     *
     * @param groupMemory
     * @return
     */
    GroupMemory selectByGroup(GroupMemory groupMemory);
}
