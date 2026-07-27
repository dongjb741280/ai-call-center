package com.voxai.cc.fs.handler;

import com.voxai.core.constant.Constant;
import com.voxai.core.constant.FsConstant;
import com.voxai.core.entity.SipGateway;
import com.voxai.core.mapper.SipGatewayMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.fs.event.FsCustomEvent;
import com.voxai.cc.fs.handler.base.BaseEventHandler;

import java.time.Instant;

/**
 * Created by dongjb on 2025/09/03
 */
@Component
@HandlerType(FsConstant.CUSTOM)
public class FsCustomHandler extends BaseEventHandler<FsCustomEvent> {

    @Autowired
    private SipGatewayMapper sipGatewayMapper;

    @Override
    public void handleEvent(FsCustomEvent event) {
        switch (event.getSubclass()) {
            case "sofia::register":
                gatewayRegister(event);
                break;
            case "sofia::unregister":
                unRegister(event);
                break;
            default:
                break;
        }
    }


    /**
     * 网关注册(模拟网关或者数字中继网关)
     *
     * @param event
     */
    private void gatewayRegister(FsCustomEvent event) {
        if (event.getUsername() == null || Constant.UNKNOWN.equals(event.getUsername()) || Constant.FORBIDDEN.equals(event.getAuthResult())) {
            return;
        }
        //网关内网
        String internal = null;
        if (event.getCallid().contains(Constant.AT)) {
            internal = event.getCallid().split(Constant.AT)[1] + Constant.CO + event.getNetworkPort();
        }
        //网关外网
        String external = event.getSipIp() + Constant.CO + event.getNetworkPort();
        SipGateway sipGateway = sipGatewayMapper.selectByUsername(event.getUsername());
        if (sipGateway == null || sipGateway.getStatus() == 0) {
            return;
        }
        sipGateway.setInternal(internal);
        sipGateway.setExternal(external);
        sipGateway.setStatus(2);
        if (StringUtils.isNumeric(event.getExpires())) {
            sipGateway.setExpire(Integer.parseInt(event.getExpires()));
        }
        sipGateway.setRegisterTime(Instant.now().getEpochSecond());
        sipGatewayMapper.updateByPrimaryKeySelective(sipGateway);
        String protocol = event.getStatus().toUpperCase().contains("UDP") ? "udp" : "tcp";
        logger.info("username:{} register {}:{} success, internal:{}  external:{} expire:{}", event.getUsername(), protocol, sipGateway.getRegisterAddr(), internal, external, event.getExpires());
    }

    /**
     * 网关取消注册
     *
     * @param event
     */
    private void unRegister(FsCustomEvent event) {
        logger.info("user {} unRegister", event.getUsername());
        SipGateway sipGateway = sipGatewayMapper.selectByUsername(event.getUsername());
        if (sipGateway == null) {
            return;
        }
        sipGateway.setInternal(Constant.EMPTY);
        sipGateway.setExternal(Constant.EMPTY);
        sipGateway.setRegisterTime(0L);
        sipGateway.setExpire(0);
        sipGateway.setStatus(1);
        sipGateway.setUts(Instant.now().getEpochSecond());
        sipGatewayMapper.updateByPrimaryKeySelective(sipGateway);
    }
}
