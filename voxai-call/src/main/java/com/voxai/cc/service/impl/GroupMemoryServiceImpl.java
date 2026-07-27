package com.voxai.cc.service.impl;

import com.voxai.core.entity.GroupMemory;
import com.voxai.core.mapper.GroupMemoryMapper;
import com.voxai.core.mapper.base.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.voxai.cc.service.GroupMemoryService;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Service
public class GroupMemoryServiceImpl extends BaseServiceImpl<GroupMemory> implements GroupMemoryService {

    @Autowired
    private GroupMemoryMapper groupMemoryMapper;

    @Override
    BaseMapper<GroupMemory> baseMapper() {
        return groupMemoryMapper;
    }


    @Override
    public GroupMemory selectByGroup(GroupMemory groupMemory) {
        return groupMemoryMapper.selectByGroup(groupMemory);
    }
}
