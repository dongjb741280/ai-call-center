package com.voxai.core.vo;

import javax.validation.constraints.NotNull;
import java.util.List;

/** 坐席所属技能组和主技能组，统一写入 cc_agent_group。 */
public class AgentGroupsVo {
    @NotNull(message = "所属技能组不能为空")
    private List<Long> groupIds;

    private Long groupId;

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
