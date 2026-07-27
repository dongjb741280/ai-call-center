/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.api.service;

import com.voxai.core.entity.AgentGroup;
import com.voxai.core.entity.Group;
import com.voxai.core.po.GroupInfo;
import com.voxai.core.vo.GroupInfoVo;

import java.util.List;

public interface GroupService extends BaseService<Group> {


    /**
     * 技能组详情
     *
     * @param companyId
     * @param id
     * @return
     */
    GroupInfo getGroupInfo(Long companyId, Long id);

    /**
     * 增加或修改技能组
     *
     * @param groupInfoVo
     * @return
     */
    int saveOrUpdateGroup(GroupInfoVo groupInfoVo);


    /**
     * 删除技能组
     *
     * @param companyId
     * @param id
     * @return
     */
    int deleteGroup(Long companyId, Long id);

    /**
     * 技能组下坐席
     *
     * @param companyId
     * @param groupId
     * @return
     */
    List<AgentGroup> groupAgentList(Long companyId, Long groupId);
}
