package com.voxai.core.mapper;

import com.voxai.core.entity.TaskPause;
import java.util.List;

public interface TaskPauseMapper {
    List<TaskPause> selectByTaskId(Long taskId);
}
