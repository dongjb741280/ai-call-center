package com.voice9.api.web;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.voice9.core.entity.AdminRole;
import com.voice9.core.entity.Company;
import com.voice9.core.entity.AdminUser;
import com.voice9.core.entity.SipGateway;
import com.voice9.core.enums.ErrorCode;
import com.voice9.core.po.AdminAccountInfo;
import com.voice9.core.po.CommonResponse;
import com.voice9.core.po.CompanyInfo;
import com.voice9.core.po.RolePo;
import com.voice9.core.vo.CompanyVo;
import com.voice9.core.vo.SipGatewayReq;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.voice9.api.vo.server.MenuVo;
import com.voice9.api.vo.server.RoleMenuVo;
import com.voice9.api.vo.server.RoleVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by dongjb on 2025/09/03
 * <p>
 * 超管操作
 */
@RestController
@RequestMapping("admin")
public class AdminController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired(required = false)
    private MinioClient minioClient;

    @Value("${voice9.minio.server:}")
    private String ossServer;
    @Value("${voice9.minio.endpoint:}")
    private String minioEndpoint;

    /**
     * 1.2.1 菜单列表
     *
     * @param adminAccountInfo
     * @return
     */
    @GetMapping("menu")
    public CommonResponse menusList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, null, query);
        return new CommonResponse(adminService.menusList(params));
    }

    /**
     * 1.2.2 菜单树状结构
     *
     * @param adminAccountInfo
     * @return
     */
    @GetMapping("menu/tree")
    public CommonResponse menuTree(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, null, query);
        return new CommonResponse(adminService.getMenuTree(params));
    }

    /**
     * 1.2.3 添加/修改菜单
     *
     * @param adminAccountInfo
     * @param menusVo
     * @return
     */

    @PostMapping("menu")
    public CommonResponse saveOrUpdateMenus(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @Validated @RequestBody MenuVo menusVo) {
        return new CommonResponse(adminService.saveOrUpdateMenus(menusVo));
    }

    /**
     * 1.2.4 删除菜单
     *
     * @param adminAccountInfo
     * @param menuId
     * @return
     */
    @DeleteMapping("menu/{menuId}")
    public CommonResponse deleteMenus(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable String menuId) {
        return new CommonResponse(adminService.deleteMenus(menuId));
    }


    /**
     * 1.3.1 角色列表
     *
     * @param adminAccountInfo
     * @param pageInfo
     * @param query
     * @return
     */
    @GetMapping("role")
    public CommonResponse<Page<AdminRole>> getRoleList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        return new CommonResponse(adminService.getRoleList(params));
    }


    /**
     * 1.3.2 添加/修改角色
     *
     * @param adminAccountInfo
     * @return
     */
    @PostMapping("role")
    public CommonResponse addRole(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @Validated @RequestBody RoleVo roleVo) {
        return new CommonResponse(adminService.saveOrUpdateRole(roleVo));
    }

    /**
     * 1.3.3 获取角色菜单
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @GetMapping("role/{id}")
    public CommonResponse<RolePo> getRole(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        return new CommonResponse<>(adminService.getRoleMenus(id));

    }

    /**
     * 1.3.4 角色绑定菜单
     *
     * @param adminAccountInfo
     * @return
     */
    @PostMapping("roleMenu")
    public CommonResponse roleMenu(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @Validated @RequestBody RoleMenuVo roleMenuVo) {
        return new CommonResponse(adminService.roleBindMenu(roleMenuVo));
    }

    /**
     * 1.3.5 删除角色
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @DeleteMapping("role/{id}")
    public CommonResponse deleteRole(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        return new CommonResponse(adminService.deleteRole(id));
    }


    /**
     * 1.4.1 企业列表
     *
     * @param pageInfo
     * @param query
     * @return
     */
    @GetMapping("company")
    public CommonResponse<com.voice9.core.page.Page<Company>> companyList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        return new CommonResponse(companyService.findByPageParams(params));
    }

    /**
     * 1.4.2 企业详情
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @GetMapping("company/{id}")
    public CommonResponse<CompanyInfo> getCompany(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        if (adminAccountInfo.getCompanyId() != 1) {
            return new CommonResponse(ErrorCode.ACCOUNT_AUTH_ERROR);
        }
        return new CommonResponse(companyService.getCompanyInfo(adminAccountInfo.getCompanyId() == 1L ? id : adminAccountInfo.getCompanyId()));
    }

    /**
     * 1.4.3 添加企业
     *
     * @param adminAccountInfo
     * @param companyVo
     * @return
     */
    @PostMapping("company")
    public CommonResponse addCompany(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @Validated @RequestBody CompanyVo companyVo) {
        companyService.addCompany(companyVo);
        return new CommonResponse();
    }

    /**
     * 1.4.4 修改企业
     *
     * @param adminAccountInfo
     * @param company
     * @return
     */
    @PutMapping("company/{id}")
    public CommonResponse updateCompany(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id, @Validated @RequestBody Company company) {
        if (adminAccountInfo.getCompanyId() != 1) {
            return new CommonResponse(ErrorCode.ACCOUNT_AUTH_ERROR);
        }
        company.setId(id);
        companyService.updateCompany(company);
        return new CommonResponse();
    }


    /**
     * 1.4.5 删除企业
     *
     * @param adminAccountInfo
     * @return
     */
    @DeleteMapping("company/{id}")
    public CommonResponse deleteCompany(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        if (adminAccountInfo.getCompanyId() != 1) {
            return new CommonResponse(ErrorCode.ACCOUNT_AUTH_ERROR);
        }
        return new CommonResponse(companyService.deleteCompany(id));
    }


    /**
     * 1.11.1 sip网关列表
     *
     * @param adminAccountInfo
     * @param pageInfo
     * @param query
     * @return
     */
    @GetMapping("sipGateway")
    public CommonResponse<PageInfo<SipGateway>> sipGatewayList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        return new CommonResponse(adminService.sipGatewayList(params));
    }

    /**
     * 1.11.2 添加或修改sip网关
     *
     * @param adminAccountInfo
     * @param sipGatewayReq
     * @return
     */
    @PostMapping("sipGateway")
    public CommonResponse saveOrUpdateSipGateway(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @RequestBody @Validated SipGatewayReq sipGatewayReq) {
        return new CommonResponse(adminService.saveOrUpdateSipGateway(sipGatewayReq));
    }

    /**
     * 1.11.3 删除sip网关
     *
     * @param adminAccountInfo
     * @param ids
     * @return
     */
    @DeleteMapping("sipGateway")
    public CommonResponse deleteSipGateway(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @RequestBody List<Long> ids) {
        return new CommonResponse(adminService.deleteSipGateway(ids));
    }


    /**
     * 1.5.1 用户列表
     */
    @GetMapping("user")
    public CommonResponse<PageInfo<AdminUser>> userList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        return new CommonResponse(adminService.findByPageParams(params));
    }

    /**
     * 1.5.2 新增用户
     */
    @PostMapping("user")
    public CommonResponse<Long> addUser(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @RequestBody AdminUser adminUser) {
        // 绑定企业
        if (adminAccountInfo.getCompanyId() != 1L) {
            adminUser.setCompanyId(adminAccountInfo.getBindCompanyId());
        }
        long now = java.time.Instant.now().getEpochSecond();
        adminUser.setCts(now);
        adminUser.setUts(now);
        if (adminUser.getStatus() == null) {
            adminUser.setStatus(1);
        }
        // 密码加密（如果提供）
        if (adminUser.getPasswd() != null && !adminUser.getPasswd().isEmpty()) {
            adminUser.setPasswd(com.voice9.api.util.BcryptUtil.encrypt(adminUser.getPasswd()));
        }
        
        // 保存用户基本信息
        int addResult = adminService.add(adminUser);
        logger.info("User added with result: {}, userId: {}", addResult, adminUser.getId());
        
        // 保存用户角色关联
        if (adminUser.getRoleIds() != null && !adminUser.getRoleIds().isEmpty()) {
            logger.info("Saving user roles for userId: {}, roleIds: {}", adminUser.getId(), adminUser.getRoleIds());
            adminService.saveUserRoles(adminUser.getId(), adminUser.getRoleIds(), adminUser.getCompanyId());
        } else {
            logger.info("No roles to save for userId: {}", adminUser.getId());
        }
        
        return new CommonResponse(adminUser.getId());
    }

    /**
     * 1.5.3 修改用户
     */
    @PutMapping("user")
    public CommonResponse<Integer> updateUser(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @RequestBody AdminUser adminUser) {
        // 防越权：非平台账号只能改自己企业
        if (adminAccountInfo.getCompanyId() != 1L) {
            adminUser.setCompanyId(adminAccountInfo.getBindCompanyId());
        }
        adminUser.setUts(java.time.Instant.now().getEpochSecond());
        // 如果传了新密码则加密
        if (adminUser.getPasswd() != null && !adminUser.getPasswd().isEmpty()) {
            adminUser.setPasswd(com.voice9.api.util.BcryptUtil.encrypt(adminUser.getPasswd()));
        }
        
        // 更新用户基本信息
        int result = adminService.editById(adminUser);
        
        // 更新用户角色关联
        if (adminUser.getRoleIds() != null) {
            adminService.saveUserRoles(adminUser.getId(), adminUser.getRoleIds(), adminUser.getCompanyId());
        }
        
        return new CommonResponse(result);
    }

    /**
     * 1.5.4 删除用户
     */
    @DeleteMapping("user/{id}")
    public CommonResponse<Integer> deleteUser(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        return new CommonResponse(adminService.deleteById(id));
    }


    /**
     * 1.5.5 上传头像
     */
    @PostMapping(value = "user/avatar", consumes = {"multipart/form-data"})
    public CommonResponse<String> uploadAvatar(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                               @RequestParam("file") MultipartFile file) {
        if (minioClient == null) {
            logger.warn("MinIO client not configured");
            return new CommonResponse<>(ErrorCode.RUNTIME_EXCEPTION);
        }
        if (file == null || file.isEmpty()) {
            return new CommonResponse<String>(ErrorCode.PARAMETER_ERROR);
        }
        try {
            String bucket = "cc-public";
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
            String original = file.getOriginalFilename();
            String suffix = original != null && original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
            String objectName = "avatar/" + java.time.LocalDate.now().toString().replaceAll("-", "/") + "/" + java.util.UUID.randomUUID() + suffix;

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            String publicHost = (ossServer != null && !ossServer.isEmpty()) ? ossServer : minioEndpoint;
            String url = publicHost.replaceAll("/$", "") + "/" + bucket + "/" + objectName;
            return new CommonResponse<String>(url);
        } catch (Exception e) {
            logger.error("upload avatar error", e);
            return new CommonResponse<String>(ErrorCode.RUNTIME_EXCEPTION.getCode(), e.getMessage(), null);
        }
    }
}
