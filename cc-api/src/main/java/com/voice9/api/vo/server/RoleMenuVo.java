package com.voice9.api.vo.server;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by dongjb on 2025/09/03
 */
public class RoleMenuVo {

    @NotNull
    private Long roleId;

    private List<String> menuIds;


    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public List<String> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(List<String> menuIds) {
        this.menuIds = menuIds;
    }
}
