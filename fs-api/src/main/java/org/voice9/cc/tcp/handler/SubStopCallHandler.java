package org.voice9.cc.tcp.handler;

import org.springframework.stereotype.Component;
import org.voice9.cc.tcp.handler.base.SubBaseHandler;
import org.voice9.cc.configration.HandlerType;
import org.voice9.cc.tcp.event.SubStopCallEvent;

/**
 * Create by dongjb on 2025/09/03
 */

@Component
@HandlerType("SUB_STOP_CALL")
public class SubStopCallHandler extends SubBaseHandler<SubStopCallEvent> {
    @Override
    public void handleEvent(SubStopCallEvent event) {

    }
}
