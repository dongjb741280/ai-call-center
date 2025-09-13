package com.voice9.core.po;

import com.voice9.core.entity.Skill;
import com.voice9.core.entity.SkillAgent;

import java.util.List;

/**
 * Created by dongjb on 2025/09/03
 */
public class SkillInfo extends Skill {

    /**
     * 技能中的坐席
     */
    private List<SkillAgent> skillAgents;

    public List<SkillAgent> getSkillAgents() {
        return skillAgents;
    }

    public void setSkillAgents(List<SkillAgent> skillAgents) {
        this.skillAgents = skillAgents;
    }
}
