package com.voice9.core.mapper;

import com.voice9.core.entity.AdminUser;
import com.voice9.core.mapper.base.BaseMapper;

import java.util.List;
import java.util.Map;

public interface AdminUserMapper extends BaseMapper<AdminUser> {

    /**
     * 查询坐席
     *
     * @param username
     * @return
     */
    AdminUser adminLogin(String username);


    /**
     * 根据角色查询账号
     *
     * @param roleId
     * @return
     */
    List<AdminUser> selectByRoleId(Long roleId);

    /**
     * 分页查询用户列表（带企业名称、角色名称）
     */
    List<AdminUser> selectListByMap(Map<String, Object> params);
}