package com.voxai.cc.tcp.handler;

import org.springframework.stereotype.Component;
import com.voxai.cc.tcp.handler.base.SubBaseHandler;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.tcp.event.SubStopCallEvent;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
@HandlerType("SUB_STOP_CALL")
public class SubStopCallHandler extends SubBaseHandler<SubStopCallEvent> {
    @Override
    public void handleEvent(SubStopCallEvent event) {

    }
}
