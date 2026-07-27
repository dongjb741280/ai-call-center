package com.voxai.cc.tcp.handler;

import org.springframework.stereotype.Component;
import com.voxai.cc.tcp.handler.base.SubBaseHandler;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.tcp.event.SubStopCallEvent;

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
