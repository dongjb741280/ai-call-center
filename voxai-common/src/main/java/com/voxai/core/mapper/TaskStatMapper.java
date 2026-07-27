package com.voxai.core.mapper;

import com.voxai.core.entity.TaskStat;

import java.util.List;
import java.util.Map;

public interface TaskStatMapper {
    List<TaskStat> selectListByMap(Map<String, Object> params);
}
