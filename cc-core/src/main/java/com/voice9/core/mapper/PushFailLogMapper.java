package com.voice9.core.mapper;

import com.voice9.core.entity.PushLog;
import com.voice9.core.mapper.base.BaseMapper;

public interface PushFailLogMapper extends BaseMapper<PushLog> {

    int deletePushFailLog(Long cts);

    int pushSuccess(PushLog pushLog);
}
