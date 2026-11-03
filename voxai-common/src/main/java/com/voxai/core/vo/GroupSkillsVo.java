package com.voxai.core.vo;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/** Skill routing configuration saved independently from the rest of a group. */
public class GroupSkillsVo {
    @NotNull(message = "技能组技能不能为空")
    @Size(max = 10, message = "技能组技能不能超过10个")
    @Valid
    private List<SkillGroupVo> skillList;

    public List<SkillGroupVo> getSkillList() { return skillList; }
    public void setSkillList(List<SkillGroupVo> skillList) { this.skillList = skillList; }
}
