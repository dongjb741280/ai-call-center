package com.voice9.core.mapper;

import com.voice9.core.entity.TaskContact;
import java.util.List;

public interface TaskContactMapper {
    List<TaskContact> selectByTaskId(Long taskId);
}
