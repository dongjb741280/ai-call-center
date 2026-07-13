package com.voice9.api.web;


import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.voice9.api.vo.excel.AgentImportExcel;
import com.voice9.core.entity.*;
import com.voice9.core.mapper.VdnCodeMapper;
import com.voice9.core.mapper.VdnConfigMapper;
import com.voice9.core.mapper.VdnScheduleMapper;
import com.voice9.core.mapper.VdnPhoneMapper;
import com.voice9.core.mapper.OverflowConfigMapper;
import com.voice9.core.mapper.OverflowExpMapper;
import com.voice9.core.mapper.IvrWorkflowMapper;
import com.voice9.core.po.*;
import com.voice9.core.vo.*;
import com.voice9.core.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by dongjb on 2025/09/03
 */
@RestController
@RequestMapping("config")
public class CompanyController extends BaseController {
    protected Logger logger = LoggerFactory.getLogger(CompanyController.class);

    @Autowired
    private VdnCodeMapper vdnCodeMapper;

    @Autowired
    private VdnConfigMapper vdnConfigMapper;

    @Autowired
    private VdnScheduleMapper vdnScheduleMapper;

    @Autowired
    private VdnPhoneMapper vdnPhoneMapper;

    @Autowired
    private OverflowConfigMapper overflowConfigMapper;

    @Autowired
    private OverflowExpMapper overflowExpMapper;

    @Autowired
    private IvrWorkflowMapper ivrWorkflowMapper;




    /**
     * 1.3.1 sip列表
     *
     * @param adminAccountInfo
     * @param pageInfo
     * @param query
     * @return
     */
    @GetMapping("sip")
    public CommonResponse<PageInfo<AgentSipPo>> sipList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        return new CommonResponse<PageInfo<AgentSipPo>>(agentService.agentSipList(params));
    }

    /**
     * 1.3.2 添加sip号码
     *
     * @param adminAccountInfo
     * @param agentSipVo
     * @return
     */
    @PostMapping("sip")
    public CommonResponse addSip(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @Validated @RequestBody AgentSipVo agentSipVo) {
        agentSipVo.setCompanyId(adminAccountInfo.getBindCompanyId());
        agentSipVo.setId(null);
        agentService.saveOrUpdateAgentSip(agentSipVo);
        return new CommonResponse<>();
    }

    /**
     * 1.3.3 修改sip号
     *
     * @param adminAccountInfo
     * @param id
     * @param agentSipVo
     * @return
     */
    @PutMapping("sip/{id}")
    public CommonResponse updateSip(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                    @PathVariable Long id, @Validated @RequestBody AgentSipVo agentSipVo) {
        agentSipVo.setCompanyId(adminAccountInfo.getBindCompanyId());
        agentSipVo.setId(id);
        agentService.saveOrUpdateAgentSip(agentSipVo);
        return new CommonResponse<>();
    }

    /**
     * 1.3.4 删除sip
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @DeleteMapping("sip/{id}")
    public CommonResponse deleteSip(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        agentService.deleteSip(adminAccountInfo.getBindCompanyId(), id);
        return new CommonResponse<>();
    }


    /**
     * 1.4.1 显号列表
     *
     * @param adminAccountInfo
     * @param pageInfo
     * @param query
     * @return
     */
    @GetMapping("phone")
    public CommonResponse<PageInfo<CompanyPhone>> phoneList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        return new CommonResponse<PageInfo<CompanyPhone>>(displayService.findByPageParams(params));
    }

    /**
     * 1.4.2 添加显号
     *
     * @param adminAccountInfo
     * @param companyPhoneVo
     * @return
     */
    @PostMapping("phone")
    public CommonResponse addPhone(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @Validated @RequestBody CompanyPhoneVo companyPhoneVo) {
        if (adminAccountInfo.getCompanyId() != 1) {
            companyPhoneVo.setCompanyId(adminAccountInfo.getBindCompanyId());
        }
        displayService.saveOrUpdatePhone(companyPhoneVo);
        return new CommonResponse();
    }

    /**
     * 1.4.3 修改显号
     *
     * @param adminAccountInfo
     * @param id
     * @param companyPhoneVo
     * @return
     */
    @PutMapping("phone/{id}")
    public CommonResponse updatePhone(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                      @PathVariable Long id, @Validated @RequestBody CompanyPhoneVo companyPhoneVo) {
        if (adminAccountInfo.getCompanyId() != 1) {
            companyPhoneVo.setCompanyId(adminAccountInfo.getBindCompanyId());
        }
        companyPhoneVo.setId(id);
        displayService.saveOrUpdatePhone(companyPhoneVo);
        return new CommonResponse();
    }

    /**
     * 1.4.4 删除显号
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @DeleteMapping("phone/{id}")
    public CommonResponse deletePhone(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                      @PathVariable Long id) {
        displayService.deletePhone(adminAccountInfo.getCompanyId() == 1 ? null : adminAccountInfo.getBindCompanyId(), id);
        return new CommonResponse();
    }

    /**
     * 1.4.5 显号池列表
     *
     * @param adminAccountInfo
     * @param pageInfo
     * @param query
     * @return
     */
    @GetMapping("display")
    public CommonResponse<PageInfo<CompanyDisplayPo>> displayGroupList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        return new CommonResponse<PageInfo<CompanyDisplayPo>>(displayService.findDisplayByPage(params));
    }

    /**
     * 1.4.6 获取池列表详情
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @GetMapping("display/{id}")
    public CommonResponse<CompanyDisplayPo> displayGroup(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        return new CommonResponse<CompanyDisplayPo>(displayService.getDisplay(adminAccountInfo.getBindCompanyId() == 1L ? null : adminAccountInfo.getBindCompanyId(), id));
    }

    /**
     * 1.4.7 添加号码池
     *
     * @param adminAccountInfo
     * @param displayGroupVo
     * @return
     */
    @PostMapping("display")
    public CommonResponse displayGroupList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                           @Validated @RequestBody DisplayGroupVo displayGroupVo) {
        displayGroupVo.setId(null);
        if (adminAccountInfo.getCompanyId() != 1) {
            displayGroupVo.setCompanyId(adminAccountInfo.getBindCompanyId());
        }
        displayService.saveOrUpdateDisplay(displayGroupVo);
        return new CommonResponse();
    }

    /**
     * 1.4.8 号码池更新号码
     *
     * @param adminAccountInfo
     * @param displayGroupVo
     * @return
     */
    @PutMapping("display/{id}")
    public CommonResponse displayGroupUpdate(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                             @PathVariable Long id, @Validated @RequestBody DisplayGroupVo displayGroupVo) {
        displayGroupVo.setId(id);
        if (adminAccountInfo.getCompanyId() != 1) {
            displayGroupVo.setCompanyId(adminAccountInfo.getBindCompanyId());
        }
        displayService.saveOrUpdateDisplay(displayGroupVo);
        return new CommonResponse();
    }

    /**
     * 1.4.9 删除号码池
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @DeleteMapping("display/{id}")
    public CommonResponse deleteDisplayGroup(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                             @PathVariable Long id) {
        displayService.deleteDisplay(adminAccountInfo.getBindCompanyId(), id);
        return new CommonResponse();
    }


    /*******************************************************************************/
    /****************字冠、网关、网关组设置：新增加网关，再增加网关组，再增加字冠**************/
    /****************其中字冠是按企业划分，有全局的字冠，每个企业也可以新建企业字冠************/
    /*******************************************************************************/

    /**
     * 1.5.1 网关列表
     *
     * @param adminAccountInfo
     * @param pageInfo
     * @param query
     * @return
     */
    @GetMapping("routegetway")
    public CommonResponse<PageInfo<RouteGetway>> routeGetwayList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        return new CommonResponse<PageInfo<RouteGetway>>(routeService.findRouteGetwayByPage(params));
    }

    /**
     * 1.5.2 网关详情
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @GetMapping("routegetway/{id}")
    public CommonResponse<RouteGetway> routeGetway(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        return new CommonResponse<RouteGetway>(routeService.findRoutewayById(id));
    }

    /**
     * 1.5.3 添加网关
     *
     * @param adminAccountInfo
     * @param routeGetwayVo
     * @return
     */
    @PostMapping("routegetway")
    public CommonResponse<RouteGetway> addRouteGetway(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                                      @Validated @RequestBody RouteGetwayVo routeGetwayVo) {
        routeGetwayVo.setId(null);
        routeService.saveOrUpdateRouteGetway(routeGetwayVo);
        return new CommonResponse<>();
    }

    /**
     * 1.5.4 修改网关
     *
     * @param adminAccountInfo
     * @param id
     * @param routeGetwayVo
     * @return
     */
    @PutMapping("routegetway/{id}")
    public CommonResponse updateRouteGetway(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id,
                                            @Validated @RequestBody RouteGetwayVo routeGetwayVo) {
        routeGetwayVo.setId(id);
        return new CommonResponse(routeService.saveOrUpdateRouteGetway(routeGetwayVo));
    }

    /**
     * 1.5.5 删除网关
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @DeleteMapping("routegetway/{id}")
    public CommonResponse delRouteGetway(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        return new CommonResponse(routeService.delRoutewayById(id));
    }

    /**
     * 1.5.6 网关组列表
     *
     * @param adminAccountInfo
     * @param pageInfo
     * @param query
     * @return
     */
    @GetMapping("routegroup")
    public CommonResponse<PageInfo<RouteGroup>> routeGroupList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        return new CommonResponse<PageInfo<RouteGroup>>(routeService.findRouteGroupByPage(params));
    }

    /**
     * 1.5.7 网关组详情
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @GetMapping("routegroup/{id}")
    public CommonResponse<RouteGroupPo> routeGroup(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        return new CommonResponse<RouteGroupPo>(routeService.findRouteGroup(id));
    }

    /**
     * 1.5.8 添加网关组
     *
     * @param adminAccountInfo
     * @param routeGroupVo
     * @return
     */
    @PostMapping("routegroup")
    public CommonResponse addRouteGroup(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @Validated @RequestBody RouteGroupVo routeGroupVo) {
        routeGroupVo.setId(null);
        return new CommonResponse<>(routeService.saveOrUpdateRouteGroup(routeGroupVo));
    }

    /**
     * 1.5.9 修改网关组
     *
     * @param adminAccountInfo
     * @param routeGroupVo
     * @return
     */
    @PutMapping("routegroup/{id}")
    public CommonResponse updateRouteGroup(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id, @Validated @RequestBody RouteGroupVo routeGroupVo) {
        routeGroupVo.setId(id);
        return new CommonResponse<>(routeService.saveOrUpdateRouteGroup(routeGroupVo));
    }

    /**
     * 1.5.10 删除网关组
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @DeleteMapping("routegroup/{id}")
    public CommonResponse deleteRouteGroup(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        return new CommonResponse<>(routeService.deleteRouteGroup(id));
    }

    /**
     * 1.5.11 字冠列表
     *
     * @param adminAccountInfo
     * @param pageInfo
     * @param query
     * @return
     */
    @GetMapping("routecall")
    public CommonResponse<PageInfo<RouteCallInfo>> routeCallList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        return new CommonResponse<PageInfo<RouteCallInfo>>(routeService.findRouteCallByPage(params));
    }

    /**
     * 1.5.12 字冠详情
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @GetMapping("routecall/{id}")
    public CommonResponse<RouteCallInfo> routeCallList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("companyId", adminAccountInfo.getBindCompanyId());
        return new CommonResponse<RouteCallInfo>(routeService.findRouteCall(params));
    }

    /**
     * 1.5.13 新增字冠
     *
     * @param adminAccountInfo
     * @param routeCallVo
     * @return
     */
    @PostMapping("routecall")
    public CommonResponse addRouteCall(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                       @Validated @RequestBody RouteCallVo routeCallVo) {
        if (adminAccountInfo.getCompanyId() != 1) {
            routeCallVo.setCompanyId(adminAccountInfo.getBindCompanyId());
        }
        routeCallVo.setId(null);
        return new CommonResponse(routeService.saveOrUpdateRouteCall(routeCallVo));
    }

    /**
     * 1.5.14 修改字冠
     *
     * @param adminAccountInfo
     * @param routeCallVo
     * @return
     */
    @PutMapping("routecall/{id}")
    public CommonResponse updateRouteCall(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id,
                                          @Validated @RequestBody RouteCallVo routeCallVo) {
        routeCallVo.setCompanyId(adminAccountInfo.getBindCompanyId());
        routeCallVo.setId(id);
        return new CommonResponse(routeService.saveOrUpdateRouteCall(routeCallVo));
    }

    /**
     * 1.5.15 删除字冠
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @DeleteMapping("routecall/{id}")
    public CommonResponse deleteRouteCall(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        return new CommonResponse(routeService.deleteRouteCall(adminAccountInfo.getBindCompanyId(), id));
    }

    /**
     * 1.6.1 技能组列表
     *
     * @param adminAccountInfo
     * @param pageInfo
     * @param query
     * @return
     */
    @GetMapping("group")
    public CommonResponse<Page<GroupInfo>> groupList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        return new CommonResponse(groupService.findByPageParams(params));
    }

    /**
     * 1.6.2 技能组详情
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @GetMapping("group/{id}")
    public CommonResponse<GroupInfo> groupInfo(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        return new CommonResponse(groupService.getGroupInfo(adminAccountInfo.getCompanyId() == 1L ? null : adminAccountInfo.getCompanyId(), id));
    }

    /**
     * 1.6.3 添加技能组
     *
     * @param adminAccountInfo
     * @param groupInfoVo
     * @return
     */
    @PostMapping("group")
    public CommonResponse addGroup(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                   @Validated @RequestBody GroupInfoVo groupInfoVo) {
        groupInfoVo.setCompanyId(adminAccountInfo.getBindCompanyId());
        return new CommonResponse(groupService.saveOrUpdateGroup(groupInfoVo));
    }


    /**
     * 1.6.5 删除技能组
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @DeleteMapping("group/{id}")
    public CommonResponse updateGroup(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        return new CommonResponse(groupService.deleteGroup(adminAccountInfo.getBindCompanyId(), id));
    }

    /**
     * 1.6.6 技能组下坐席
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @GetMapping("group/agent/{id}")
    public CommonResponse groupAgent(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        return new CommonResponse(groupService.groupAgentList(adminAccountInfo.getBindCompanyId(), id));
    }


    /**
     * 1.7.1 技能列表
     *
     * @param adminAccountInfo
     * @param pageInfo
     * @param query
     * @return
     */
    @GetMapping("skill")
    public CommonResponse<PageInfo<Skill>> skillList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        return new CommonResponse(skillService.findByPageParams(params));
    }

    /**
     * 1.7.2 技能详情
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @GetMapping("skill/{id}")
    public CommonResponse<SkillInfo> skill(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        return new CommonResponse<>(skillService.skillInfo(adminAccountInfo.getCompanyId(), id));
    }


    /**
     * 1.7.3 新增技能
     *
     * @param adminAccountInfo
     * @param skillVo
     * @return
     */
    @PostMapping("skill")
    public CommonResponse addSkill(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                   @Validated @RequestBody SkillVo skillVo) {
        skillVo.setCompanyId(adminAccountInfo.getBindCompanyId());
        return new CommonResponse<>(skillService.saveOrUpdateSkill(skillVo));
    }


    /**
     * 1.7.4 技能新增坐席
     *
     * @param adminAccountInfo
     * @param id
     * @param skillAgentsVo
     * @return
     */
    @PostMapping("skill/agent/{id}")
    public CommonResponse addSkillAgent(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                        @PathVariable Long id, @Validated @RequestBody SkillAgentsVo skillAgentsVo) {
        skillAgentsVo.setCompanyId(adminAccountInfo.getCompanyId());
        skillAgentsVo.setSkillId(id);
        return new CommonResponse(skillService.addSkillAgent(skillAgentsVo));
    }

    /**
     * 1.7.5 技能删除坐席
     *
     * @param adminAccountInfo
     * @param id
     * @param skillAgentsVo
     * @return
     */
    @DeleteMapping("skill/agent/{id}")
    public CommonResponse deleteSkillAgent(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                           @PathVariable Long id, @Validated @RequestBody SkillAgentsVo skillAgentsVo) {
        skillAgentsVo.setCompanyId(adminAccountInfo.getCompanyId());
        skillAgentsVo.setSkillId(id);
        return new CommonResponse(skillService.deleteSkippAgent(skillAgentsVo));
    }


    /**
     * 1.7.6 技能修改坐席
     *
     * @param adminAccountInfo
     * @param id
     * @param skillAgentVo
     * @return
     */
    @PutMapping("skill/agent/{id}")
    public CommonResponse updateSkillAgent(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                           @PathVariable Long id, @Validated @RequestBody SkillAgentVo skillAgentVo) {
        skillAgentVo.setId(id);
        skillAgentVo.setCompanyId(adminAccountInfo.getCompanyId());
        return new CommonResponse(skillService.updateSkillAgent(skillAgentVo));
    }

    /**
     * 1.7.7 修改技能
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @PutMapping("skill/{id}")
    public CommonResponse updateSkill(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                      @PathVariable Long id, @Validated @RequestBody SkillVo skillVo) {
        skillVo.setId(id);
        skillVo.setCompanyId(adminAccountInfo.getBindCompanyId());
        return new CommonResponse<>(skillService.saveOrUpdateSkill(skillVo));
    }

    /**
     * 1.7.8 删除技能
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @DeleteMapping("skill/{id}")
    public CommonResponse deleteSkill(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                      @PathVariable Long id) {
        Skill skill = new Skill();
        skill.setCompanyId(adminAccountInfo.getBindCompanyId());
        skill.setId(id);
        return new CommonResponse<>(skillService.deleteSkill(skill));
    }

    /**
     * 1.8.1 坐席列表
     *
     * @param adminAccountInfo
     * @param pageInfo
     * @param query
     * @return
     */
    @GetMapping("agent")
    public CommonResponse<PageInfo<Agent>> agentList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        return new CommonResponse(agentService.findByPageParams(params));
    }

    /**
     * 1.8.2 坐席详情
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @GetMapping("agent/{id}")
    public CommonResponse<AgentInfo> agentInfo(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        return new CommonResponse(agentService.getAgentInfo(adminAccountInfo.getCompanyId() == 1L ? null : adminAccountInfo.getCompanyId(), id));
    }

    /**
     * 1.8.3 添加坐席
     *
     * @param adminAccountInfo
     * @param agentVo
     * @return
     */
    @PostMapping("agent")
    public CommonResponse addAgent(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @Validated @RequestBody AgentVo agentVo) {
        agentVo.setCompanyId(adminAccountInfo.getBindCompanyId());
        agentVo.setId(null);
        return new CommonResponse<>(agentService.saveOrUpdate(agentVo));
    }

    /**
     * 1.8.4 修改坐席
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @PutMapping("agent/{id}")
    public CommonResponse updateAgent(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id, @Validated @RequestBody AgentVo agentVo) {
        agentVo.setCompanyId(adminAccountInfo.getBindCompanyId());
        agentVo.setId(id);
        return new CommonResponse<>(agentService.saveOrUpdate(agentVo));
    }

    /**
     * 1.8.5 删除坐席
     *
     * @param adminAccountInfo
     * @param id
     * @return
     */
    @DeleteMapping("agent/{id}")
    public CommonResponse deleteAgent(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @PathVariable Long id) {
        return new CommonResponse(agentService.deleteAgent(adminAccountInfo.getBindCompanyId(), id));
    }


    /**
     * 1.8.6 批量添加坐席
     *
     * @param adminAccountInfo
     * @param addAgentVo
     * @return
     */
    @PostMapping("agent/batch")
    public CommonResponse batchAddAgent(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                        @Validated @RequestBody BatchAddAgentVo addAgentVo) {
        addAgentVo.setCompanyId(adminAccountInfo.getBindCompanyId());
        return new CommonResponse<>(agentService.batchAddAgent(addAgentVo));
    }

    /**
     * 1.8.7 坐席导出
     *
     * @param response
     * @param adminAccountInfo
     * @param query
     * @throws IOException
     */
    @GetMapping("agent/export")
    public void agentExport(HttpServletResponse response,
                            @ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                            String query) throws IOException {
        Map<String, Object> params = parseMap(adminAccountInfo, null, query);
        agentService.agentExport(response, params);
    }

    /**
     * 1.8.8 坐席导入
     *
     * @param adminAccountInfo
     * @throws IOException
     */
    @PostMapping("agent/import")
    public CommonResponse agentImport(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo, @RequestParam("file") MultipartFile multipartFile) throws Exception {
        ImportParams importParams = new ImportParams();
        List<AgentImportExcel> agentImportExcels = ExcelImportUtil.importExcel(multipartFile.getInputStream(), AgentImportExcel.class, importParams);
        return new CommonResponse(agentService.agentImport(agentImportExcels, adminAccountInfo.getBindCompanyId()));
    }

    // ==================== 路由子码(VDN)管理 ====================

    /**
     * VDN列表
     */
    @GetMapping("vdn")
    @SuppressWarnings("unchecked")
    public CommonResponse<PageInfo<VdnCodePo>> vdnList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                                        PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        PageHelper.startPage((Integer) params.get("pageNum"), (Integer) params.get("pageSize"));
        PageHelper.orderBy("id desc");
        List<VdnCodePo> list = (List<VdnCodePo>) (List<?>) vdnCodeMapper.selectListByMap(params);
        return new CommonResponse<>(new PageInfo<>(list));
    }

    /**
     * VDN详情
     */
    @GetMapping("vdn/{id}")
    public CommonResponse<VdnCodePo> vdnDetail(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                                @PathVariable Long id) {
        VdnCodePo po = (VdnCodePo) vdnCodeMapper.selectByPrimaryKey(id);
        if (po == null) {
            return new CommonResponse<>(com.voice9.core.enums.ErrorCode.DATA_NOT_EXIST);
        }
        return new CommonResponse<>(po);
    }

    /**
     * 新增VDN
     */
    @PostMapping("vdn")
    public CommonResponse addVdn(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                  @RequestBody VdnCode vdnCode) {
        vdnCode.setId(null);
        vdnCode.setCompanyId(adminAccountInfo.getBindCompanyId());
        vdnCode.setCts(Instant.now().getEpochSecond());
        vdnCode.setStatus(vdnCode.getStatus() != null ? vdnCode.getStatus() : 1);
        vdnCodeMapper.insertSelective(vdnCode);
        return new CommonResponse<>();
    }

    /**
     * 修改VDN
     */
    @PutMapping("vdn/{id}")
    public CommonResponse updateVdn(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                     @PathVariable Long id, @RequestBody VdnCode vdnCode) {
        vdnCode.setId(id);
        vdnCode.setCompanyId(adminAccountInfo.getBindCompanyId());
        vdnCode.setUts(Instant.now().getEpochSecond());
        vdnCodeMapper.updateByPrimaryKeySelective(vdnCode);
        return new CommonResponse<>();
    }

    /**
     * 删除VDN
     */
    @DeleteMapping("vdn/{id}")
    public CommonResponse deleteVdn(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                     @PathVariable Long id) {
        VdnCode vdnCode = new VdnCode();
        vdnCode.setId(id);
        vdnCode.setStatus(0);
        vdnCode.setUts(Instant.now().getEpochSecond());
        vdnCodeMapper.updateByPrimaryKeySelective(vdnCode);
        return new CommonResponse<>();
    }

    // ==================== VDN子码配置管理 ====================

    /**
     * VDN子码配置列表
     */
    @GetMapping("vdn/{vdnId}/config")
    @SuppressWarnings("unchecked")
    public CommonResponse<List<VdnSchedulePo>> vdnConfigList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                                              @PathVariable Long vdnId) {
        List<VdnSchedulePo> list = (List<VdnSchedulePo>) (List<?>) vdnConfigMapper.selectByVdn(vdnId);
        return new CommonResponse<>(list);
    }

    /**
     * 新增VDN子码配置
     */
    @PostMapping("vdn/{vdnId}/config")
    public CommonResponse addVdnConfig(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                        @PathVariable Long vdnId, @RequestBody VdnConfig vdnConfig) {
        vdnConfig.setId(null);
        vdnConfig.setVdnId(vdnId);
        vdnConfig.setCompanyId(adminAccountInfo.getBindCompanyId());
        vdnConfig.setCts(Instant.now().getEpochSecond());
        vdnConfig.setStatus(vdnConfig.getStatus() != null ? vdnConfig.getStatus() : 1);
        vdnConfigMapper.insertSelective(vdnConfig);
        return new CommonResponse<>();
    }

    /**
     * 修改VDN子码配置
     */
    @PutMapping("vdn/config/{id}")
    public CommonResponse updateVdnConfig(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                           @PathVariable Long id, @RequestBody VdnConfig vdnConfig) {
        vdnConfig.setId(id);
        vdnConfig.setCompanyId(adminAccountInfo.getBindCompanyId());
        vdnConfig.setUts(Instant.now().getEpochSecond());
        vdnConfigMapper.updateByPrimaryKeySelective(vdnConfig);
        return new CommonResponse<>();
    }

    /**
     * 删除VDN子码配置
     */
    @DeleteMapping("vdn/config/{id}")
    public CommonResponse deleteVdnConfig(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                           @PathVariable Long id) {
        VdnConfig vdnConfig = new VdnConfig();
        vdnConfig.setId(id);
        vdnConfig.setStatus(0);
        vdnConfig.setUts(Instant.now().getEpochSecond());
        vdnConfigMapper.updateByPrimaryKeySelective(vdnConfig);
        return new CommonResponse<>();
    }

    // ==================== 日程管理 ====================

    /**
     * 日程列表
     */
    @GetMapping("schedule")
    @SuppressWarnings("unchecked")
    public CommonResponse<PageInfo<VdnSchedule>> scheduleList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                                               PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        PageHelper.startPage((Integer) params.get("pageNum"), (Integer) params.get("pageSize"));
        PageHelper.orderBy("id desc");
        List<VdnSchedule> list = (List<VdnSchedule>) (List<?>) vdnScheduleMapper.selectListByMap(params);
        return new CommonResponse<>(new PageInfo<>(list));
    }

    /**
     * 日程详情
     */
    @GetMapping("schedule/{id}")
    public CommonResponse<VdnSchedule> scheduleDetail(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                                       @PathVariable Long id) {
        VdnSchedule schedule = vdnScheduleMapper.selectByPrimaryKey(id);
        if (schedule == null) {
            return new CommonResponse<>(com.voice9.core.enums.ErrorCode.DATA_NOT_EXIST);
        }
        return new CommonResponse<>(schedule);
    }

    /**
     * 新增日程
     */
    @PostMapping("schedule")
    public CommonResponse addSchedule(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                       @RequestBody VdnSchedule schedule) {
        schedule.setId(null);
        schedule.setCompanyId(adminAccountInfo.getBindCompanyId());
        schedule.setCts(Instant.now().getEpochSecond());
        schedule.setStatus(schedule.getStatus() != null ? schedule.getStatus() : 1);
        vdnScheduleMapper.insertSelective(schedule);
        return new CommonResponse<>();
    }

    /**
     * 修改日程
     */
    @PutMapping("schedule/{id}")
    public CommonResponse updateSchedule(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                          @PathVariable Long id, @RequestBody VdnSchedule schedule) {
        schedule.setId(id);
        schedule.setCompanyId(adminAccountInfo.getBindCompanyId());
        schedule.setUts(Instant.now().getEpochSecond());
        vdnScheduleMapper.updateByPrimaryKeySelective(schedule);
        return new CommonResponse<>();
    }

    /**
     * 删除日程
     */
    @DeleteMapping("schedule/{id}")
    public CommonResponse deleteSchedule(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                          @PathVariable Long id) {
        VdnSchedule schedule = new VdnSchedule();
        schedule.setId(id);
        schedule.setStatus(0);
        schedule.setUts(Instant.now().getEpochSecond());
        vdnScheduleMapper.updateByPrimaryKeySelective(schedule);
        return new CommonResponse<>();
    }

    // ==================== 接入号码管理 ====================

    /**
     * 接入号码列表
     */
    @GetMapping("vdnPhone")
    @SuppressWarnings("unchecked")
    public CommonResponse<PageInfo<VdnPhone>> vdnPhoneList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                                            PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        PageHelper.startPage((Integer) params.get("pageNum"), (Integer) params.get("pageSize"));
        PageHelper.orderBy("id desc");
        List<VdnPhone> list = (List<VdnPhone>) (List<?>) vdnPhoneMapper.selectListByMap(params);
        return new CommonResponse<>(new PageInfo<>(list));
    }

    /**
     * 接入号码详情
     */
    @GetMapping("vdnPhone/{id}")
    public CommonResponse<VdnPhone> vdnPhoneDetail(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                                     @PathVariable Long id) {
        VdnPhone phone = vdnPhoneMapper.selectByPrimaryKey(id);
        if (phone == null) {
            return new CommonResponse<>(com.voice9.core.enums.ErrorCode.DATA_NOT_EXIST);
        }
        return new CommonResponse<>(phone);
    }

    /**
     * 新增接入号码
     */
    @PostMapping("vdnPhone")
    public CommonResponse addVdnPhone(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                       @RequestBody VdnPhone vdnPhone) {
        vdnPhone.setId(null);
        vdnPhone.setCompanyId(adminAccountInfo.getBindCompanyId());
        vdnPhone.setCts(Instant.now().getEpochSecond());
        vdnPhone.setStatus(vdnPhone.getStatus() != null ? vdnPhone.getStatus() : 1);
        vdnPhoneMapper.insertSelective(vdnPhone);
        return new CommonResponse<>();
    }

    /**
     * 修改接入号码
     */
    @PutMapping("vdnPhone/{id}")
    public CommonResponse updateVdnPhone(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                          @PathVariable Long id, @RequestBody VdnPhone vdnPhone) {
        vdnPhone.setId(id);
        vdnPhone.setCompanyId(adminAccountInfo.getBindCompanyId());
        vdnPhone.setUts(Instant.now().getEpochSecond());
        vdnPhoneMapper.updateByPrimaryKeySelective(vdnPhone);
        return new CommonResponse<>();
    }

    /**
     * 删除接入号码
     */
    @DeleteMapping("vdnPhone/{id}")
    public CommonResponse deleteVdnPhone(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                          @PathVariable Long id) {
        VdnPhone vdnPhone = new VdnPhone();
        vdnPhone.setId(id);
        vdnPhone.setStatus(0);
        vdnPhone.setUts(Instant.now().getEpochSecond());
        vdnPhoneMapper.updateByPrimaryKeySelective(vdnPhone);
        return new CommonResponse<>();
    }

    // ==================== 排队策略管理 ====================

    /**
     * 排队策略列表
     */
    @GetMapping("overflow")
    @SuppressWarnings("unchecked")
    public CommonResponse<PageInfo<OverflowConfig>> overflowList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                                                  PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        PageHelper.startPage((Integer) params.get("pageNum"), (Integer) params.get("pageSize"));
        PageHelper.orderBy("id desc");
        List<OverflowConfig> list = (List<OverflowConfig>) (List<?>) overflowConfigMapper.selectListByMap(params);
        return new CommonResponse<>(new PageInfo<>(list));
    }

    /**
     * 排队策略详情
     */
    @GetMapping("overflow/{id}")
    public CommonResponse<OverflowConfig> overflowDetail(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                                          @PathVariable Long id) {
        return new CommonResponse<>(overflowConfigMapper.selectByPrimaryKey(id));
    }

    /**
     * 新增排队策略
     */
    @PostMapping("overflow")
    public CommonResponse addOverflow(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                       @RequestBody OverflowConfig config) {
        config.setId(null);
        config.setCompanyId(adminAccountInfo.getBindCompanyId());
        config.setCts(Instant.now().getEpochSecond());
        overflowConfigMapper.insertSelective(config);
        return new CommonResponse<>();
    }

    /**
     * 修改排队策略
     */
    @PutMapping("overflow/{id}")
    public CommonResponse updateOverflow(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                          @PathVariable Long id, @RequestBody OverflowConfig config) {
        config.setId(id);
        config.setCompanyId(adminAccountInfo.getBindCompanyId());
        config.setUts(Instant.now().getEpochSecond());
        overflowConfigMapper.updateByPrimaryKeySelective(config);
        return new CommonResponse<>();
    }

    /**
     * 删除排队策略
     */
    @DeleteMapping("overflow/{id}")
    public CommonResponse deleteOverflow(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                          @PathVariable Long id) {
        overflowConfigMapper.deleteByPrimaryKey(id);
        return new CommonResponse<>();
    }

    // ==================== 排队策略前置条件 ====================

    /**
     * 前置条件列表
     */
    @GetMapping("overflow/{overflowId}/exp")
    public CommonResponse<List<OverflowExp>> overflowExpList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                                              @PathVariable Long overflowId) {
        return new CommonResponse<>(overflowExpMapper.selectByOverflowId(overflowId));
    }

    /**
     * 新增前置条件
     */
    @PostMapping("overflow/{overflowId}/exp")
    public CommonResponse addOverflowExp(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                          @PathVariable Long overflowId, @RequestBody OverflowExp exp) {
        exp.setId(null);
        exp.setOverflowId(overflowId);
        exp.setCompanyId(adminAccountInfo.getBindCompanyId());
        exp.setCts(Instant.now().getEpochSecond());
        exp.setStatus(exp.getStatus() != null ? exp.getStatus() : 1);
        overflowExpMapper.insertSelective(exp);
        return new CommonResponse<>();
    }

    /**
     * 修改前置条件
     */
    @PutMapping("overflow/exp/{id}")
    public CommonResponse updateOverflowExp(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                             @PathVariable Long id, @RequestBody OverflowExp exp) {
        exp.setId(id);
        exp.setCompanyId(adminAccountInfo.getBindCompanyId());
        exp.setUts(Instant.now().getEpochSecond());
        overflowExpMapper.updateByPrimaryKeySelective(exp);
        return new CommonResponse<>();
    }

    /**
     * 删除前置条件
     */
    @DeleteMapping("overflow/exp/{id}")
    public CommonResponse deleteOverflowExp(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                             @PathVariable Long id) {
        OverflowExp exp = new OverflowExp();
        exp.setId(id);
        exp.setStatus(0);
        exp.setUts(Instant.now().getEpochSecond());
        overflowExpMapper.updateByPrimaryKeySelective(exp);
        return new CommonResponse<>();
    }

    // ==================== IVR管理 ====================

    /**
     * IVR列表
     */
    @GetMapping("ivr")
    @SuppressWarnings("unchecked")
    public CommonResponse<PageInfo<IvrWorkflow>> ivrList(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                                          PageInfo pageInfo, String query) {
        Map<String, Object> params = parseMap(adminAccountInfo, pageInfo, query);
        PageHelper.startPage((Integer) params.get("pageNum"), (Integer) params.get("pageSize"));
        PageHelper.orderBy("id desc");
        List<IvrWorkflow> list = (List<IvrWorkflow>) (List<?>) ivrWorkflowMapper.selectListByMap(params);
        return new CommonResponse<>(new PageInfo<>(list));
    }

    /**
     * IVR详情
     */
    @GetMapping("ivr/{id}")
    public CommonResponse<IvrWorkflow> ivrDetail(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                                  @PathVariable Long id) {
        return new CommonResponse<>(ivrWorkflowMapper.selectByPrimaryKey(id));
    }

    /**
     * 新增IVR
     */
    @PostMapping("ivr")
    public CommonResponse addIvr(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                  @RequestBody IvrWorkflow ivr) {
        ivr.setId(null);
        ivr.setCompanyId(adminAccountInfo.getBindCompanyId());
        ivr.setCts(Instant.now().getEpochSecond());
        ivrWorkflowMapper.insertSelective(ivr);
        return new CommonResponse<>();
    }

    /**
     * 修改IVR
     */
    @PutMapping("ivr/{id}")
    public CommonResponse updateIvr(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                     @PathVariable Long id, @RequestBody IvrWorkflow ivr) {
        ivr.setId(id);
        ivr.setCompanyId(adminAccountInfo.getBindCompanyId());
        ivr.setUts(Instant.now().getEpochSecond());
        ivrWorkflowMapper.updateByPrimaryKeySelective(ivr);
        return new CommonResponse<>();
    }

    /**
     * 删除IVR
     */
    @DeleteMapping("ivr/{id}")
    public CommonResponse deleteIvr(@ModelAttribute("adminAccountInfo") AdminAccountInfo adminAccountInfo,
                                     @PathVariable Long id) {
        ivrWorkflowMapper.deleteByPrimaryKey(id);
        return new CommonResponse<>();
    }

}
