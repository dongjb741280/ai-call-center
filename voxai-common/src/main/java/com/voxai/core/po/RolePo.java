package com.voxai.core.po;

import com.voxai.core.entity.AdminRole;

import java.util.List;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class RolePo extends AdminRole {

    /**
     * 菜单权限集合
     */
    private List<MenusPo> adminMenuList;

    public List<MenusPo> getAdminMenuList() {
        return adminMenuList;
    }

    public void setAdminMenuList(List<MenusPo> adminMenuList) {
        this.adminMenuList = adminMenuList;
    }
}
