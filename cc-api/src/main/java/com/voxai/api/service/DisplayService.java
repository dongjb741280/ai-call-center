package com.voxai.api.service;

import com.github.pagehelper.PageInfo;
import com.voxai.core.entity.CompanyPhone;
import com.voxai.core.po.CompanyDisplayPo;
import com.voxai.core.vo.CompanyPhoneVo;
import com.voxai.core.vo.DisplayGroupVo;

import java.util.Map;

/**
 * 显号号码
 * <p>
 * Created by dongjb on 2025/09/03
 */
public interface DisplayService extends BaseService<CompanyPhone> {

    /**
     * 添加企业号码
     *
     * @param companyPhoneVo
     * @return
     */
    int saveOrUpdatePhone(CompanyPhoneVo companyPhoneVo);

    /**
     * 删除显号
     *
     * @param companyId
     * @param id
     * @return
     */
    int deletePhone(Long companyId, Long id);


    /**
     * 号码池分页
     *
     * @param params
     * @return
     */
    PageInfo<CompanyDisplayPo> findDisplayByPage(Map<String, Object> params);

    /**
     * 获取号码池详情
     *
     * @param companyId
     * @param id
     * @return
     */
    CompanyDisplayPo getDisplay(Long companyId, Long id);

    /**
     * 新增修改号码池
     *
     * @param displayGroupVo
     * @return
     */
    int saveOrUpdateDisplay(DisplayGroupVo displayGroupVo);

    /**
     * 删除号码池
     *
     * @param companyId
     * @param id
     * @return
     */
    int deleteDisplay(Long companyId, Long id);
}
