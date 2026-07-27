package com.voxai.core.mapper;

import com.voxai.core.entity.TaskContact;
import java.util.List;

public interface TaskContactMapper {
    List<TaskContact> selectByTaskId(Long taskId);
}
