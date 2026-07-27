package com.voxai.core.mapper;

import com.voxai.core.entity.AdminUserRole;
import com.voxai.core.mapper.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface AdminUserRoleMapper extends BaseMapper<AdminUserRole> {

    /**
     * 根据用户ID删除用户角色关联
     * @param userId 用户ID
     * @return 删除的记录数
     */
    int deleteByUserId(@Param("userId") Long userId);

}