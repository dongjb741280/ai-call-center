package com.voxai.cc.tcp.handler;

import org.springframework.stereotype.Component;
import com.voxai.cc.tcp.event.SubMakeCallEvent;
import com.voxai.cc.tcp.handler.base.SubBaseHandler;
import com.voxai.cc.configration.HandlerType;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
@HandlerType("SUB_MAKE_CALL")
public class SubMakeCallHandler extends SubBaseHandler<SubMakeCallEvent> {

    @Override
    public void handleEvent(SubMakeCallEvent event) {

    }
}
