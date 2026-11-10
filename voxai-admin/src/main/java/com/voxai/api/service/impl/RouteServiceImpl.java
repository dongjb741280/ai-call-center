/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.voxai.api.service.RouteService;
import com.voxai.core.entity.RouteCall;
import com.voxai.core.entity.RouteGetway;
import com.voxai.core.entity.RouteGetwayGroup;
import com.voxai.core.entity.RouteGroup;
import com.voxai.core.enums.ErrorCode;
import com.voxai.core.mapper.RouteCallMapper;
import com.voxai.core.mapper.RouteGetwayGroupMapper;
import com.voxai.core.mapper.RouteGetwayMapper;
import com.voxai.core.mapper.RouteGroupMapper;
import com.voxai.core.mapper.base.BaseMapper;
import com.voxai.core.po.RouteCallInfo;
import com.voxai.core.po.RouteGroupPo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.voxai.api.exception.BusinessException;
import com.voxai.core.vo.RouteCallVo;
import com.voxai.core.vo.RouteGetwayVo;
import com.voxai.core.vo.RouteGroupVo;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RouteServiceImpl extends BaseServiceImpl<RouteCallInfo> implements RouteService {

    @Autowired
    private RouteCallMapper routeCallMapper;

    @Autowired
    private RouteGroupMapper routeGroupMapper;

    @Autowired
    private RouteGetwayGroupMapper routeGetwayGroupMapper;

    @Autowired
    private RouteGetwayMapper routeGetwayMapper;

    @Override
    BaseMapper<RouteCallInfo> baseMapper() {
        return routeCallMapper;
    }

    @Override
    public PageInfo<RouteGetway> findRouteGetwayByPage(Map<String, Object> params) {
        Integer pageNum = (Integer) params.get("pageNum");
        Integer pageSize = (Integer) params.get("pageSize");
        PageHelper.startPage(pageNum, pageSize);
        List<RouteGetway> list = routeGetwayMapper.selectListByMap(params);
        return new PageInfo<>(list);
    }

    @Override
    public RouteGetway findRoutewayById(Long id) {
        RouteGetway routeGetway = routeGetwayMapper.selectByPrimaryKey(id);
        if (routeGetway == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST);
        }
        return routeGetway;
    }

    @Override
    public int delRoutewayById(Long id) {
        RouteGetway routeGetway = routeGetwayMapper.selectByPrimaryKey(id);
        if (routeGetway == null || routeGetway.getStatus() == 0) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST);
        }
        //判断是否被引用
        RouteGroupPo routeGroupPo = routeGroupMapper.selectByGetwayId(id);
        if (routeGroupPo != null) {
            throw new BusinessException(ErrorCode.DATA_IS_USED);
        }
        routeGetway.setUts(Instant.now().getEpochSecond());
        routeGetway.setStatus(0);
        routeGetway.setName(routeGetway.getName() + randomDelete());
        return routeGetwayMapper.updateByPrimaryKeySelective(routeGetway);
    }

    @Override
    public int saveOrUpdateRouteGetway(RouteGetwayVo routeGetwayVo) {
        RouteGetway routeGetway = new RouteGetway();
        BeanUtils.copyProperties(routeGetwayVo, routeGetway);
        //判断数据是否存在
        Map<String, Object> params = new HashMap<>();
        params.put("routeName", routeGetwayVo.getName());
        List<RouteGetway> list = routeGetwayMapper.selectListByMap(params);
        if (!CollectionUtils.isEmpty(list)) {
            if (routeGetway.getId() == null ||
                    (routeGetway.getId() != null && !routeGetway.getId().equals(list.get(0).getId()))) {
                throw new BusinessException(ErrorCode.DUPLICATE_EXCEPTION);
            }
        }

        if (routeGetwayVo.getId() == null) {
            routeGetway.setCts(Instant.now().getEpochSecond());
            return routeGetwayMapper.insertSelective(routeGetway);
        }
        RouteGetway exist = routeGetwayMapper.selectByPrimaryKey(routeGetwayVo.getId());
        if (exist == null) {
            new BusinessException(ErrorCode.DATA_NOT_EXIST);
        }
        routeGetway.setUts(Instant.now().getEpochSecond());
        return routeGetwayMapper.updateByPrimaryKeySelective(routeGetway);
    }


    @Override
    public PageInfo<RouteGroup> findRouteGroupByPage(Map<String, Object> params) {
        Integer pageNum = (Integer) params.get("pageNum");
        Integer pageSize = (Integer) params.get("pageSize");
        PageHelper.startPage(pageNum, pageSize);
        List<RouteGroup> list = routeGroupMapper.selectListByMap(params);
        return new PageInfo<>(list);
    }

    @Override
    public RouteGroupPo findRouteGroup(Long id) {
        return routeGroupMapper.selectById(id);
    }

    @Override
    public PageInfo<RouteCallInfo> findRouteCallByPage(Map<String, Object> params) {
        Integer pageNum = (Integer) params.get("pageNum");
        Integer pageSize = (Integer) params.get("pageSize");
        PageHelper.startPage(pageNum, pageSize);
        List<RouteCallInfo> list = routeCallMapper.selectListByMap(params);
        return new PageInfo<>(list);
    }

    @Override
    public RouteCallInfo findRouteCall(Map<String, Object> params) {
        List<RouteCallInfo> list = routeCallMapper.selectListByMap(params);
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST);
        }
        return routeCallMapper.selectListByMap(params).get(0);
    }

    @Override
    public int saveOrUpdateRouteCall(RouteCallVo routeCallVo) {
        if (routeCallVo.getCompanyId() == null) routeCallVo.setCompanyId(0L);
        if (routeCallVo.getRouteNum() == null || !routeCallVo.getRouteNum().matches("\\*|[+#0-9]{1,32}")) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR.getCode(), "字冠必须是号码前缀或*");
        }
        if (routeCallVo.getNumMin() == null || routeCallVo.getNumMax() == null
                || routeCallVo.getNumMin() < 1 || routeCallVo.getNumMax() > 32
                || routeCallVo.getNumMax() < routeCallVo.getNumMin()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR.getCode(), "字冠匹配长度错误");
        }
        validateRewrite(routeCallVo.getCallerChange(), routeCallVo.getCallerChangeNum());
        validateRewrite(routeCallVo.getCalledChange(), routeCallVo.getCalledChangeNum());
        if (routeCallVo.getCalledChange() != null && routeCallVo.getCalledChange() > routeCallVo.getNumMin()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR.getCode(), "被叫删除位数不能大于最小号码长度");
        }
        RouteGroupPo routeGroup = routeGroupMapper.selectById(routeCallVo.getRouteGroupId());
        if (routeGroup == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST.getCode(), "路由组id不存在");
        }

        //判断数据是否存在
        Map<String, Object> params = new HashMap<>();
        params.put("name", routeCallVo.getRouteNum());
        params.put("companyId", routeCallVo.getCompanyId());
        List<RouteCall> list = routeCallMapper.selectList(params);
        if (!CollectionUtils.isEmpty(list)) {
            if (routeCallVo.getId() == null ||
                    (routeCallVo.getId() != null && !routeCallVo.getId().equals(list.get(0).getId()))) {
                throw new BusinessException(ErrorCode.DUPLICATE_EXCEPTION);
            }
        }
        RouteCall routeCall = new RouteCall();
        BeanUtils.copyProperties(routeCallVo, routeCall);
        if (routeCall.getId() == null) {
            routeCall.setStatus(1);
            routeCall.setCts(Instant.now().getEpochSecond());
            return routeCallMapper.insertSelective(routeCall);
        }
        RouteCall exist = routeCallMapper.selectByPrimaryKey(routeCall.getId());
        if (exist == null || exist.getStatus() == 0) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST);
        }
        routeCall.setUts(Instant.now().getEpochSecond());
        return routeCallMapper.updateByPrimaryKeySelective(routeCall);
    }

    @Override
    public int saveOrUpdateRouteGroup(RouteGroupVo routeGroupVo) {
        //先验证网关是不是都存在
        List<Long> getwayIds = routeGroupVo.getRouteGetways();
        if (CollectionUtils.isEmpty(getwayIds)) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        if (new java.util.HashSet<>(getwayIds).size() != getwayIds.size()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR.getCode(), "网关不能重复");
        }
        String strategy = routeGroupVo.getSelectionStrategy();
        if (strategy != null && !java.util.Arrays.asList("ROUND_ROBIN", "WEIGHTED", "FAILOVER").contains(strategy)) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR.getCode(), "无效的网关选择策略");
        }
        if (routeGroupVo.getGatewayWeights() != null) {
            routeGroupVo.getGatewayWeights().forEach((id, weight) -> {
                if (!getwayIds.contains(id) || weight == null || weight < 1 || weight > 1000) {
                    throw new BusinessException(ErrorCode.PARAMETER_ERROR.getCode(), "网关权重必须为1-1000且属于该组");
                }
            });
        }
        List<RouteGetway> routeGetways = routeGetwayMapper.selectByIds(getwayIds);
        if (routeGetways.size() != getwayIds.size()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR);
        }
        // Preserve the operator's order for primary/backup selection.
        routeGetways.sort(java.util.Comparator.comparingInt(gateway -> getwayIds.indexOf(gateway.getId())));
        //判断名称是否重复
        Map<String, Object> params = new HashMap<>();
        params.put("routeGroupName", routeGroupVo.getRouteGroup());
        List<RouteGroup> routeGroupList = routeGroupMapper.selectListByMap(params);
        if (!CollectionUtils.isEmpty(routeGroupList)) {
            RouteGroup exist = routeGroupList.get(0);
            if (routeGroupVo.getId() == null || !exist.getId().equals(routeGroupVo.getId())) {
                throw new BusinessException(ErrorCode.DUPLICATE_EXCEPTION);
            }
        }


        if (routeGroupVo.getId() == null) {
            RouteGroup routeGroup = new RouteGroup();
            BeanUtils.copyProperties(routeGroupVo, routeGroup);
            routeGroupMapper.insertSelective(routeGroup);
            routeGroupList = routeGroupMapper.selectListByMap(params);
            Long id = routeGroupList.get(0).getId();
            //插入关联关系
            RouteGetwayGroup routeGetwayGroup = null;
            for (RouteGetway routeGetway : routeGetways) {
                routeGetwayGroup = new RouteGetwayGroup();
                routeGetwayGroup.setCts(Instant.now().getEpochSecond());
                routeGetwayGroup.setUts(null);
                routeGetwayGroup.setGetwayId(routeGetway.getId());
                routeGetwayGroup.setRouteGroupId(id);
                routeGetwayGroup.setWeight(routeGroupVo.getGatewayWeights() == null ? 1
                        : routeGroupVo.getGatewayWeights().getOrDefault(routeGetway.getId(), 1));
                routeGetwayGroupMapper.insertSelective(routeGetwayGroup);
            }
            return 1;
        }
        //修改，先判断是不是存在
        RouteGroup routeGroup = routeGroupMapper.selectByPrimaryKey(routeGroupVo.getId());
        if (routeGroup == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST);
        }
        routeGroup = new RouteGroup();
        BeanUtils.copyProperties(routeGroupVo, routeGroup);
        routeGroup.setCts(null);
        routeGroup.setUts(Instant.now().getEpochSecond());
        routeGroupMapper.updateByPrimaryKeySelective(routeGroup);
        //删除之前绑定的
        routeGetwayGroupMapper.deleteByRoutegroup(routeGroup.getId());
        RouteGetwayGroup routeGetwayGroup = null;
        for (RouteGetway routeGetway : routeGetways) {
            routeGetwayGroup = new RouteGetwayGroup();
            routeGetwayGroup.setCts(Instant.now().getEpochSecond());
            routeGetwayGroup.setUts(null);
            routeGetwayGroup.setGetwayId(routeGetway.getId());
            routeGetwayGroup.setRouteGroupId(routeGroup.getId());
            routeGetwayGroup.setWeight(routeGroupVo.getGatewayWeights() == null ? 1
                    : routeGroupVo.getGatewayWeights().getOrDefault(routeGetway.getId(), 1));
            routeGetwayGroupMapper.insertSelective(routeGetwayGroup);
        }
        return 1;
    }

    private void validateRewrite(Integer strip, String replacement) {
        if (strip != null && (strip < 0 || strip > 32)
                || replacement != null && !replacement.matches("[+#0-9]{0,32}")) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR.getCode(), "号码替换须为0-32位删除长度及数字前缀");
        }
    }

    @Override
    public int deleteRouteCall(Long companyId, Long id) {
        //判断数据是否存在
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("companyId", companyId);
        List<RouteCall> list = routeCallMapper.selectList(params);
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST);
        }
        RouteCall routeCall = list.get(0);
        routeCall.setStatus(0);
        routeCall.setUts(Instant.now().getEpochSecond());
        routeCall.setRouteNum(routeCall.getRouteNum() + randomDelete());
        return routeCallMapper.updateByPrimaryKeySelective(routeCall);
    }

    @Override
    public int deleteRouteGroup(Long id) {
        RouteGroup routeGroup = routeGroupMapper.selectByPrimaryKey(id);
        if (routeGroup == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_EXIST);
        }
        //判断字冠是否引用
        List<RouteGetway> routeGetwayList = routeGetwayMapper.selectByGroupId(id);
        if (!CollectionUtils.isEmpty(routeGetwayList)) {
            throw new BusinessException(ErrorCode.DATA_IS_USED);
        }
        routeGroup.setUts(Instant.now().getEpochSecond());
        routeGroup.setStatus(0);
        routeGroup.setRouteGroup(routeGroup.getRouteGroup() + randomDelete());
        return routeGroupMapper.updateByPrimaryKeySelective(routeGroup);
    }
}
