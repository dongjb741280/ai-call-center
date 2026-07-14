package com.voice9.core.mapper;

import com.voice9.core.entity.IvrFlow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IvrFlowMapper {
    List<IvrFlow> selectByCallId(@Param("callId") Long callId);
}
