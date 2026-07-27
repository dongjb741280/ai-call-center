package com.voxai.core.mapper;

import com.voxai.core.entity.TaskField;
import com.voxai.core.mapper.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskFieldMapper extends BaseMapper<TaskField> {
    List<TaskField> selectBySourceId(Long sourceId);
    void clearSourceId(Long sourceId);
    void batchSetSourceId(@Param("sourceId") Long sourceId, @Param("ids") List<Long> ids);
}
