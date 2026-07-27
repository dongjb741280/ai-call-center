/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.PushLog;
import com.voxai.core.mapper.base.BaseMapper;

public interface PushFailLogMapper extends BaseMapper<PushLog> {

    int deletePushFailLog(Long cts);

    int pushSuccess(PushLog pushLog);
}
