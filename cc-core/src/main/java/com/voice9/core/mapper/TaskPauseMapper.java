package com.voice9.core.mapper;

import com.voice9.core.entity.TaskPause;
import java.util.List;

public interface TaskPauseMapper {
    List<TaskPause> selectByTaskId(Long taskId);
}
