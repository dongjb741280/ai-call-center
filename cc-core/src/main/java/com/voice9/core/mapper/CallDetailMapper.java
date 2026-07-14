package com.voice9.core.mapper;

import com.voice9.core.entity.CallDetail;
import com.voice9.core.mapper.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CallDetailMapper extends BaseMapper<CallDetail> {

    CallDetail selectByCallId(Long callId, String month);

    List<CallDetail> selectListByCallId(@Param("callId") Long callId, @Param("monthTime") String monthTime);

    /**
     * 迁移表
     *
     * @param month
     */
    void createNewTable(@Param("month") String month);


    /**
     * 删除数据
     *
     * @param end
     */
    int clearTable( @Param("end") Long end);
}