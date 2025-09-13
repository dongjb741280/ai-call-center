package org.voice9.cc.tcp.handler;

import org.springframework.stereotype.Component;
import org.voice9.cc.configration.HandlerType;
import org.voice9.cc.tcp.event.SubStartCallEvent;
import org.voice9.cc.tcp.handler.base.SubBaseHandler;

/**
 * Create by dongjb on 2025/09/03
 */
@Component
@HandlerType("SUB_START_CALL")
public class SubStartCallHandler extends SubBaseHandler<SubStartCallEvent> {
    @Override
    public void handleEvent(SubStartCallEvent event) {

    }
}
