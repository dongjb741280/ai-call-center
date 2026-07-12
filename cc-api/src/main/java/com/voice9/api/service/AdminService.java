package com.voice9.api.service;

import com.github.pagehelper.PageInfo;
import com.voice9.core.entity.AdminMenu;
import com.voice9.core.entity.AdminRole;
import com.voice9.core.entity.AdminUser;
import com.voice9.core.entity.AiEngine;
import com.voice9.core.entity.BlackPhone;
import com.voice9.core.entity.PhoneArea;
import com.voice9.core.entity.SipGateway;
import com.voice9.core.entity.Station;
import com.voice9.core.po.AdminLoginResult;
import com.voice9.core.po.MenusPo;
import com.voice9.core.po.RolePo;
import com.voice9.core.vo.AdminLogin;
import com.voice9.api.vo.server.MenuVo;
import com.voice9.api.vo.server.RoleMenuVo;
import com.voice9.api.vo.server.RoleVo;
import com.voice9.core.vo.SipGatewayReq;

import java.util.List;
import java.util.Map;

/**
 * Created by dongjb on 2025/09/03
 */
public interface AdminService extends BaseService<AdminUser> {

    /**
     * 账号登录
     *
     * @param adminLogin
     * @return
     */
    AdminLoginResult login(AdminLogin adminLogin);

    /**
     * @param token
     * @return
     */
    Boolean logout(String token);

    /**
     * 获取菜单列表
     *
     * @return
     */
    List<AdminMenu> menusList(Map<String, Object> params);

    /**
     * 获取菜单树状结构
     *
     * @param params
     * @return
     */
    List<MenusPo> getMenuTree(Map<String, Object> params);

    /**
     * 添加/修改菜单
     *
     * @param menusVo
     * @return
     */
    Integer saveOrUpdateMenus(MenuVo menusVo);

    /**
     * 删除菜单
     *
     * @param menuId
     * @return
     */
    Integer deleteMenus(String menuId);

    /**
     * 获取角色菜单
     *
     * @param id
     * @return
     */
    RolePo getRoleMenus(Long id);

    /**
     * 角色列表
     *
     * @param params
     * @return
     */
    PageInfo<AdminRole> getRoleList(Map<String, Object> params);


    /**
     * 添加/修改角色
     *
     * @param roleVo
     * @return
     */
    Integer saveOrUpdateRole(RoleVo roleVo);

    /**
     * @param id
     * @return
     */
    Integer deleteRole(Long id);

    /**
     * 角色绑定菜单
     *
     * @param roleMenuVo
     * @return
     */
    Integer roleBindMenu(RoleMenuVo roleMenuVo);


    /**
     * sip网关
     *
     * @param params
     * @return
     */
    PageInfo<SipGateway> sipGatewayList(Map<String, Object> params);

    /**
     * 添加或修改sip网关
     *
     * @param sipGatewayReq
     * @return
     */
    Long saveOrUpdateSipGateway(SipGatewayReq sipGatewayReq);

    /**
     * 删除sip网关
     *
     * @param ids
     * @return
     */
    int deleteSipGateway(List<Long> ids);

    /**
     * 保存用户角色关联
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @param companyId 企业ID
     * @return
     */
    Integer saveUserRoles(Long userId, List<Long> roleIds, Long companyId);

    /**
     * 号码归属地列表
     */
    PageInfo<PhoneArea> phoneAreaList(Map<String, Object> params);

    /**
     * 黑名单列表
     */
    PageInfo<BlackPhone> blackPhoneList(Map<String, Object> params);

    /**
     * 保存黑名单
     */
    int saveBlackPhone(BlackPhone blackPhone);

    /**
     * 删除黑名单
     */
    int deleteBlackPhone(Long id);

    /**
     * 模块站点列表
     */
    PageInfo<Station> stationList(Map<String, Object> params);

    /**
     * 保存模块站点
     */
    int saveStation(Station station);

    /**
     * 删除模块站点
     */
    int deleteStation(Long id);

    /**
     * AI引擎列表
     */
    PageInfo<AiEngine> engineList(Map<String, Object> params);

    /**
     * 保存AI引擎
     */
    int saveEngine(AiEngine engine);

    /**
     * 删除AI引擎
     */
    int deleteEngine(Long id);

}
