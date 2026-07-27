/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.Group;
import com.voxai.core.mapper.base.BaseMapper;
import com.voxai.core.po.GroupInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GroupMapper extends BaseMapper<Group> {

    /**
     * @param params
     * @return
     */
    List<GroupInfo> selectGroupInfoList(Map<String, Object> params);

    /**
     * @param params
     * @return
     */
    GroupInfo selectGroupInfo(Map<String, Object> params);

    /**
     * 查询企业技能组id
     *
     * @param companyId
     * @return
     */
    List<Long> selectGroupIds(Long companyId);

    Long selectByName(@Param("companyId") Long companyId , @Param("name")String name);

}