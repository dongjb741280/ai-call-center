package com.voxai.cc.tcp.handler;

import org.springframework.stereotype.Component;
import com.voxai.cc.tcp.handler.base.SubBaseHandler;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.tcp.event.SubLoginEvent;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@HandlerType("SUB_LOGIN")
@Component
public class SubLoginHandler extends SubBaseHandler<SubLoginEvent> {
    @Override
    public void handleEvent(SubLoginEvent event) {

    }
}