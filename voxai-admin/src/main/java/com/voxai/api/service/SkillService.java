package com.voxai.api.service;

import com.voxai.core.entity.Skill;
import com.voxai.core.po.SkillInfo;
import com.voxai.core.vo.SkillAgentVo;
import com.voxai.core.vo.SkillAgentsVo;
import com.voxai.core.vo.SkillVo;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public interface SkillService extends BaseService<Skill> {

    /**
     * 技能详情
     *
     * @param companyId
     * @param id
     * @return
     */
    SkillInfo skillInfo(Long companyId, Long id);

    /**
     * 新增或修改技能
     *
     * @param skillVo
     */
    int saveOrUpdateSkill(SkillVo skillVo);

    /**
     * 删除技能
     *
     * @param skill
     * @return
     */
    int deleteSkill(Skill skill);


    /**
     * 新增坐席技能
     *
     * @param skillAgentsVo
     * @return
     */
    int addSkillAgent(SkillAgentsVo skillAgentsVo);

    /**
     * 删除坐席技能
     *
     * @param skillAgentsVo
     * @return
     */
    int deleteSkippAgent(SkillAgentsVo skillAgentsVo);


    /**
     * @param skillAgent
     * @return
     */
    int updateSkillAgent(SkillAgentVo skillAgent);


}
