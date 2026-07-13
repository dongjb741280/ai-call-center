package com.voice9.core.mapper;

import com.voice9.core.entity.TaskStat;

import java.util.List;
import java.util.Map;

public interface TaskStatMapper {
    List<TaskStat> selectListByMap(Map<String, Object> params);
}
