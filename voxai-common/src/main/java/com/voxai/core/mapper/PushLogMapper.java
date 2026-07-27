/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;

import com.voxai.core.entity.PushLog;
import com.voxai.core.mapper.base.BaseMapper;

public interface PushLogMapper extends BaseMapper<PushLog> {


    int deletePushLog(Long cts);

    /**
     * 推送成功
     * @param pushLog
     * @return
     */
    int pushSuccess(PushLog pushLog);

}