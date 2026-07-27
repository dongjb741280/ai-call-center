/**
 * @author dongjb
 * @date 2026/07/27
 */
package com.voxai.core.mapper;


import com.voxai.core.entity.SipGateway;
import com.voxai.core.mapper.base.BaseMapper;

public interface SipGatewayMapper extends BaseMapper<SipGateway> {


    SipGateway selectByUsername(String username);

    /**
     * 定时检测网关下线
     *
     * @param time
     */
    void checkSipGatewayRegister(Long time);
}