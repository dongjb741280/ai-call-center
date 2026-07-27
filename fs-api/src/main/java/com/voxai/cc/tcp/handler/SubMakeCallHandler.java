package com.voxai.cc.tcp.handler;

import org.springframework.stereotype.Component;
import com.voxai.cc.tcp.event.SubMakeCallEvent;
import com.voxai.cc.tcp.handler.base.SubBaseHandler;
import com.voxai.cc.configration.HandlerType;

/**
 * Create by dongjb on 2025/09/03
 */
@Component
@HandlerType("SUB_MAKE_CALL")
public class SubMakeCallHandler extends SubBaseHandler<SubMakeCallEvent> {

    @Override
    public void handleEvent(SubMakeCallEvent event) {

    }
}
