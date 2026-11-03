package com.voxai.core.vo;

import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.NotNull;

/** 坐席编辑中的技能配置。 */
public class AgentSkillVo {
    @NotNull(message = "请选择技能")
    private Long skillId;

    @NotNull(message = "请填写技能等级")
    @Range(min = 1, max = 100, message = "技能等级必须在1-100之间")
    private Integer rankValue;

    public Long getSkillId() { return skillId; }
    public void setSkillId(Long skillId) { this.skillId = skillId; }
    public Integer getRankValue() { return rankValue; }
    public void setRankValue(Integer rankValue) { this.rankValue = rankValue; }
}
