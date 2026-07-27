package com.voxai.core.po;

import com.voxai.core.entity.Skill;
import com.voxai.core.entity.SkillAgent;

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
