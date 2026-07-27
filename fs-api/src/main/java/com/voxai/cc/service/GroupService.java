package com.voxai.cc.service;

import com.voxai.core.entity.Group;
import com.voxai.core.po.GroupInfo;

import java.util.List;

/**
 * Create by dongjb on 2025/09/03
 */
public interface GroupService extends BaseService<Group> {

    List<GroupInfo> getGroupByConpany(Long companyId);

    /**
     * 初始化技能组排队策略
     * @param groupInfo
     */
    void initGroupStrategy(GroupInfo groupInfo);
}
