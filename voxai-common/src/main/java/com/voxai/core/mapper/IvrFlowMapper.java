/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.IvrFlow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IvrFlowMapper {
    List<IvrFlow> selectByCallId(@Param("callId") Long callId);
}
