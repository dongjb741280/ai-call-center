/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.CallDtmf;
import com.voxai.core.mapper.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface CallDtmfMapper extends BaseMapper<CallDtmf> {



    /**
     * 迁移表
     *
     * @param month
     */
    void createNewTable( @Param("month") String month);


    /**
     * 删除数据
     *
     * @param end
     */
    int clearTable(@Param("end") Long end);
}