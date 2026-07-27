/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.SkillAgent;
import com.voxai.core.mapper.base.BaseMapper;
import com.voxai.core.vo.SkillAgentsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SkillAgentMapper extends BaseMapper<SkillAgent> {

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    int batchInsert(List<SkillAgent> list);


    /**
     * 查询坐席技能
     *
     * @param companyId
     * @param id
     * @return
     */
    List<SkillAgent> selectByAgent(@Param("companyId") Long companyId, @Param("id") Long id);

    /**
     * 删除坐席技能
     *
     * @param skillAgentsVo
     * @return
     */
    int deleteSkillAgent(SkillAgentsVo skillAgentsVo);

    /**
     * 技能下关联的坐席
     *
     * @param companyId
     * @param id
     * @return
     */
    List<SkillAgent> selectBySkill(@Param("companyId") Long companyId, @Param("id") Long id);

    /**
     * 更新坐席技能
     *
     * @param entity
     * @return
     */
    int updateSkillAgent(SkillAgent entity);
}