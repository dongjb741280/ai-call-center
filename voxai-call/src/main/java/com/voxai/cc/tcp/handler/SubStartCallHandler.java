package com.voxai.cc.tcp.handler;

import org.springframework.stereotype.Component;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.tcp.event.SubStartCallEvent;
import com.voxai.cc.tcp.handler.base.SubBaseHandler;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
@HandlerType("SUB_START_CALL")
public class SubStartCallHandler extends SubBaseHandler<SubStartCallEvent> {
    @Override
    public void handleEvent(SubStartCallEvent event) {

    }
}
