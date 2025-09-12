package com.voice9.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.voice9.api.exception.BusinessException;
import com.voice9.api.service.AdminService;
import com.voice9.api.util.BcryptUtil;
import com.voice9.api.vo.server.MenuVo;
import com.voice9.api.vo.server.RoleMenuVo;
import com.voice9.api.vo.server.RoleVo;
import com.voice9.core.entity.*;
import com.voice9.core.mapper.*;
import com.voice9.core.po.*;
import com.voice9.core.vo.SipGatewayReq;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import com.voice9.core.constant.Constant;
import com.voice9.core.enums.ErrorCode;
import com.voice9.core.mapper.base.BaseMapper;
import com.voice9.core.util.AuthUtil;
import com.voice9.core.vo.AdminLogin;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.List;
import java.util.Map;

/**
 * Created by caoliang on 2022/1/7
 */
@Service
public class AdminServiceImpl extends BaseServiceImpl<AdminUser> implements AdminService {

    @Autowired
    private AdminMenuMapper adminMenuMapper;

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private AdminRoleMapper adminRoleMapper;

    @Autowired
    private AdminUserRoleMapper adminUserRoleMapper;

    @Autowired
    private SipGatewayMapper sipGatewayMapper;

    @Override
    public AdminLoginResult login(AdminLogin adminLogin) {
        AdminUser adminUser = adminUserMapper.adminLogin(adminLogin.getUsername());
        if (adminUser == null) {
            logger.warn("user:{} login error", adminLogin.getUsername());
            throw new BusinessException(ErrorCode.ACCOUNT_ERROR);
        }
        if (adminUser.getStatus() == 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }
        //验证密码
        try {
            if (!BcryptUtil.checkPwd(adminLogin.getPasswd(), adminUser.getPasswd())) {
                throw new BusinessException(ErrorCode.ACCOUNT_ERROR);
            }
        } catch (Exception e) {
            logger.error("user:{} password is error", adminLogin.getUsername());
            throw new BusinessException(ErrorCode.ACCOUNT_ERROR);
        }

        CompanyInfo companyInfo = companyMapper.selectById(adminUser.getCompanyId());
        if (companyInfo == null) {
            //企业禁用
            throw new BusinessException(ErrorCode.COMPANY_NOT_AVALIABLE);
        }
        String token = AuthUtil.createToken(adminUser.getUsername(), adminUser.getCompanyId(), companyInfo.getSecretKey());

        AdminLoginResult result = new AdminLoginResult();
        result.setCts(Instant.now().getEpochSecond());
        result.setToken(token);
        UserInfo userInfo = new UserInfo();
        userInfo.setAvatar(adminUser.getAvatar());
        userInfo.setOssServer(ossServer);
        userInfo.setUsername(adminUser.getUsername());
        userInfo.setCompanyName(companyInfo.getName());
        userInfo.setCompanyCode(companyInfo.getCompanyCode());
        userInfo.setGmt(companyInfo.getGmt());
        userInfo.setClientIp(adminLogin.getClientIp());
        result.setUserInfo(userInfo);
        result.setMenus(getMenus(adminUser.getId()));
        redisTemplate.opsForValue().set(Constant.ADMIN_TOKEN + token, result);
        return result;
    }

    @Override
    public Boolean logout(String token) {
        return redisTemplate.delete("token:" + token);
    }

    @Override
    public List<AdminMenu> menusList(Map<String, Object> params) {
        return adminMenuMapper.selectList(params);
    }

    @Override
    public List<MenusPo> getMenuTree(Map<String, Object> params) {
        // 获取所有菜单数据
        Map<String, Object> allMenusParams = new HashMap<>();
        allMenusParams.put("status", 1);
        List<AdminMenu> allMenus = adminMenuMapper.selectList(allMenusParams);
        
        if (CollectionUtils.isEmpty(allMenus)) {
            return new ArrayList<>();
        }
        
        // 构建菜单树
        return buildMenuTree(allMenus, "");
    }
    
    /**
     * 构建菜单树
     */
    private List<MenusPo> buildMenuTree(List<AdminMenu> allMenus, String parentId) {
        List<MenusPo> result = new ArrayList<>();
        
        for (AdminMenu menu : allMenus) {
            if (Objects.equals(menu.getParentId(), parentId)) {
                MenusPo menuPo = new MenusPo();
                BeanUtils.copyProperties(menu, menuPo);
                
                // 递归查找子菜单
                List<MenusPo> children = buildMenuTree(allMenus, menu.getMenuId());
                menuPo.setChilds(children);
                
                result.add(menuPo);
            }
        }
        
        // 按menu_order排序
        result.sort((a, b) -> {
            Integer orderA = a.getMenuOrder() != null ? a.getMenuOrder() : 0;
            Integer orderB = b.getMenuOrder() != null ? b.getMenuOrder() : 0;
            return orderA.compareTo(orderB);
        });
        
        return result;
    }


    private List<MenusPo> getMenus(Long uid) {
        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        params.put("menuLevel", 1);
        return userMenuIterator(params);
    }

    @Override
    public Integer saveOrUpdateMenus(MenuVo menusVo) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", menusVo.getName());
        List<AdminMenu> menusList = adminMenuMapper.selectList(params);

        AdminMenu adminMenu = new AdminMenu();
        BeanUtils.copyProperties(menusVo, adminMenu);

        if (!CollectionUtils.isEmpty(menusList)) {
            if (menusVo.getMenuId() == null || (menusVo.getMenuId() != null && !menusVo.getMenuId().equals(menusList.get(0).getMenuId()))) {
                throw new BusinessException(ErrorCode.DUPLICATE_EXCEPTION);
            }
        }

        if (StringUtils.isBlank(menusVo.getMenuId())) {
            adminMenu.setMenuId(RandomStringUtils.randomNumeric(32));
            adminMenu.setUts(Instant.now().getEpochSecond());
            adminMenu.setUts(adminMenu.getCts());
            return adminMenuMapper.insertSelective(adminMenu);
        }
        adminMenu.setUts(Instant.now().getEpochSecond());
        return adminMenuMapper.updateByPrimaryKeySelective(adminMenu);
    }

    @Override
    public Integer deleteMenus(String menuId) {
        AdminMenu adminMenu = adminMenuMapper.selectByMenuId(menuId);
        if (adminMenu == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST);
        }
        if (adminMenu.getChildNum() != null && adminMenu.getChildNum() > 0) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "子节点存在");
        }
        //删除角色绑定的菜单
        adminMenuMapper.deleteRoleMenu(menuId);
        return adminMenuMapper.deleteMenu(menuId);
    }

    @Override
    public RolePo getRoleMenus(Long id) {
        AdminRole adminRole = adminRoleMapper.selectByPrimaryKey(id);
        if (adminRole == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST);
        }
        RolePo rolePo = new RolePo();
        BeanUtils.copyProperties(adminRole, rolePo);

        //获取所有菜单树
        List<MenusPo> adminMenus = getMenuTree(null);

        //获取角色菜单
        List<String> roleMenuIds = adminMenuMapper.selectByRoleId(id);

        //递归遍历，设置选中状态
        forEachMenu(roleMenuIds, adminMenus);

        rolePo.setAdminMenuList(adminMenus);
        return rolePo;
    }

    /**
     * 递归遍历菜单
     *
     * @param roleMenuIds
     * @param adminMenus
     */
    private void forEachMenu(List<String> roleMenuIds, List<MenusPo> adminMenus) {
        if (CollectionUtils.isEmpty(adminMenus) || CollectionUtils.isEmpty(roleMenuIds)) {
            return;
        }
        for (MenusPo menusPo : adminMenus) {
            if (roleMenuIds.contains(menusPo.getMenuId())) {
                menusPo.setUid(1L);
            } else {
                menusPo.setUid(0L);
            }
            if (!CollectionUtils.isEmpty(menusPo.getChilds())) {
                forEachMenu(roleMenuIds, menusPo.getChilds());
            }
        }
    }

    /**
     * 获取菜单
     *
     * @param params
     * @return
     */
    private List<MenusPo> getAllMenus(Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>();
            params.put("menuLevel", 1);
        }
        List<AdminMenu> adminMenus = adminMenuMapper.selectList(params);
        if (CollectionUtils.isEmpty(adminMenus)) {
            return new ArrayList<>();
        }
        List<MenusPo> menusPoList = new ArrayList<>();
        for (AdminMenu child : adminMenus) {
            MenusPo childPo = new MenusPo();
            BeanUtils.copyProperties(child, childPo);
            
            // 递归查询子菜单
            Map<String, Object> childParams = new HashMap<>();
            childParams.put("parentId", child.getMenuId());
            List<MenusPo> childMenus = getAllMenus(childParams);
            childPo.setChilds(childMenus);
            
            menusPoList.add(childPo);
        }
        return menusPoList;
    }


    @Override
    public PageInfo<AdminRole> getRoleList(Map<String, Object> params) {
        Integer pageNum = (Integer) params.get("pageNum");
        Integer pageSize = (Integer) params.get("pageSize");
        PageHelper.startPage(pageNum, pageSize);
        List<AdminRole> list = adminRoleMapper.selectList(params);
        return new PageInfo<AdminRole>(list);
    }

    @Override
    public Integer saveOrUpdateRole(RoleVo roleVo) {
        Map<String, Object> params = new HashMap<>();
        params.put("companyId", roleVo.getCompanyId());
        params.put("roleName", roleVo.getRoleName());
        List<AdminRole> roleList = adminRoleMapper.selectList(params);
        if (!CollectionUtils.isEmpty(roleList)) {
            if (roleVo.getId() == null || !roleVo.getRoleName().equals(roleList.get(0).getRoleName())) {
                throw new BusinessException(ErrorCode.DUPLICATE_EXCEPTION);
            }
        }
        AdminRole adminRole = new AdminRole();
        BeanUtils.copyProperties(roleVo, adminRole);
        if (roleVo.getId() == null) {
            adminRole.setCts(Instant.now().getEpochSecond());
            adminRole.setUts(adminRole.getCts());
            return adminRoleMapper.insertSelective(adminRole);
        }
        adminRole.setUts(Instant.now().getEpochSecond());
        return adminRoleMapper.updateByPrimaryKeySelective(adminRole);
    }

    @Override
    public Integer deleteRole(Long id) {
        //角色是否存在
        RolePo adminRole = adminRoleMapper.selectRoleMenu(id);
        if (adminRole == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST);
        }

        //角色是否被管理账号引用
        List<AdminUser> adminUsers = adminUserMapper.selectByRoleId(id);
        if (!CollectionUtils.isEmpty(adminUsers)) {
            throw new BusinessException(ErrorCode.RUNTIME_DATA_EXCEPTION, "角色已经绑定账号");
        }
        //删除角色权限
        adminRoleMapper.deleteMenuByRoleId(id);
        return adminRoleMapper.deleteRole(id);
    }

    @Override
    public Integer roleBindMenu(RoleMenuVo roleMenuVo) {
        //先判断数据是否存在
        RolePo role = adminRoleMapper.selectRoleMenu(roleMenuVo.getRoleId());
        if (role == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST);
        }

        //先删除角色绑定的所有菜单
        adminRoleMapper.deleteMenuByRoleId(roleMenuVo.getRoleId());
        if (CollectionUtils.isEmpty(roleMenuVo.getMenuIds())) {
            return 0;
        }
        List<AdminRoleMenu> roleMenus = new ArrayList<>();
        for (String menuId : roleMenuVo.getMenuIds()) {
            AdminRoleMenu roleMenu = new AdminRoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleMenuVo.getRoleId());
            roleMenu.setCts(Instant.now().getEpochSecond());
            roleMenu.setUts(roleMenu.getCts());
            roleMenu.setCompanyId(role.getCompanyId());
            roleMenus.add(roleMenu);
        }
        return adminRoleMapper.batchInserRoleMenus(roleMenus);
    }

    @Override
    public PageInfo<SipGateway> sipGatewayList(Map<String, Object> params) {
        Integer pageNum = (Integer) params.get(Constant.PAGE_NUM);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);
        PageHelper.startPage(pageNum, pageSize);
        if (params.containsKey(Constant.SORT)) {
            PageHelper.orderBy(params.get(Constant.SORT).toString());
        } else {
            PageHelper.orderBy(Constant.ID_ASC);
        }
        return new PageInfo<SipGateway>(sipGatewayMapper.selectListByMap(params));
    }

    @Override
    public Long saveOrUpdateSipGateway(SipGatewayReq sipGatewayReq) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", sipGatewayReq.getUsername());
        List<SipGateway> sipGatewayList = sipGatewayMapper.selectListByMap(params);
        if (!CollectionUtils.isEmpty(sipGatewayList)) {
            if (sipGatewayReq.getId() == null || !sipGatewayReq.getId().equals(sipGatewayList.get(0).getId())) {
                throw new BusinessException(ErrorCode.DUPLICATE_EXCEPTION);
            }
        }
        SipGateway sipGateway = new SipGateway();
        BeanUtils.copyProperties(sipGatewayReq, sipGateway);

        //绑定企业
        if (sipGatewayReq.getCompanyId() != null && sipGateway.getCompanyId() > 0L) {
            Company company = companyMapper.selectById(null, sipGateway.getCompanyId());
            if (company == null || company.getStatus() < 2) {
                throw new BusinessException(ErrorCode.COMPANY_NOT_AVALIABLE);
            }
            sipGateway.setCompanyName(company.getName());
            sipGateway.setCompanyCode(company.getCompanyCode());
        } else {
            sipGateway.setCompanyName(Constant.EMPTY);
            sipGateway.setCompanyCode(Constant.EMPTY);
        }
        if (sipGatewayReq.getId() == null) {
            sipGateway.setCts(Instant.now().getEpochSecond());
            sipGateway.setUts(sipGateway.getCts());
            sipGatewayMapper.insertSelective(sipGateway);
            sipGatewayList = sipGatewayMapper.selectListByMap(params);
            Long id = sipGatewayList.get(0).getId();
            return id;
        }
        sipGateway.setUts(Instant.now().getEpochSecond());
        sipGatewayMapper.updateByPrimaryKeySelective(sipGateway);
        return sipGateway.getId();
    }

    @Override
    public int deleteSipGateway(List<Long> ids) {
        for (Long id : ids) {
            SipGateway sipGateway = sipGatewayMapper.selectByPrimaryKey(id);
            if (sipGateway == null || sipGateway.getStatus() == 0) {
                throw new BusinessException(ErrorCode.DATA_NOT_EXIST);
            }
            sipGateway.setStatus(0);
            sipGateway.setUts(Instant.now().getEpochSecond());
            sipGateway.setUsername(sipGateway.getUsername() + randomDelete());
            sipGatewayMapper.updateByPrimaryKeySelective(sipGateway);
        }
        return ids.size();
    }

    /**
     * 登录账号菜单
     *
     * @param params
     * @return
     */
    private List<MenusPo> userMenuIterator(Map<String, Object> params) {
        List<AdminMenu> adminMenus = adminMenuMapper.selectUserMenus(params);
        if (CollectionUtils.isEmpty(adminMenus)) {
            return null;
        }
        List<MenusPo> menusPoList = new ArrayList<>();
        for (AdminMenu child : adminMenus) {
            MenusPo childPo = new MenusPo();
            params.put("parentId", child.getMenuId());
            params.remove("menuLevel");
            BeanUtils.copyProperties(child, childPo);
            childPo.setChilds(userMenuIterator(params));
            menusPoList.add(childPo);
        }
        return menusPoList;
    }


    @Override
    BaseMapper<AdminUser> baseMapper() {
        return adminUserMapper;
    }

    @Override
    public Integer saveUserRoles(Long userId, List<Long> roleIds, Long companyId) {
        logger.info("saveUserRoles called with userId: {}, roleIds: {}, companyId: {}", userId, roleIds, companyId);
        
        if (userId == null || roleIds == null || roleIds.isEmpty()) {
            logger.warn("saveUserRoles: userId or roleIds is null or empty");
            return 0;
        }
        
        try {
            // 先删除用户现有的角色关联
            int deletedCount = adminUserRoleMapper.deleteByUserId(userId);
            logger.info("Deleted {} existing user roles for userId: {}", deletedCount, userId);
            
            // 插入新的角色关联
            long now = Instant.now().getEpochSecond();
            int count = 0;
            for (Long roleId : roleIds) {
                AdminUserRole userRole = new AdminUserRole();
                userRole.setAccountId(userId);
                userRole.setRoleId(roleId);
                userRole.setCompanyId(companyId);
                userRole.setCts(now);
                userRole.setUts(now);
                
                int insertResult = adminUserRoleMapper.insertSelective(userRole);
                logger.info("Inserted user role: userId={}, roleId={}, result={}", userId, roleId, insertResult);
                count++;
            }
            
            logger.info("Successfully saved {} user roles for userId: {}", count, userId);
            return count;
        } catch (Exception e) {
            logger.error("Error saving user roles for userId: {}, error: {}", userId, e.getMessage(), e);
            throw e;
        }
    }
}
