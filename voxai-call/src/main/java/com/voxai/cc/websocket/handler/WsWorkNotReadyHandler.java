package com.voxai.cc.websocket.handler;

import org.springframework.stereotype.Component;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.websocket.handler.base.WsBaseHandler;
import com.voxai.cc.websocket.event.WsWorkNotReadyEvent;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
@HandlerType("WS_WORK_NOT_READY")
public class WsWorkNotReadyHandler extends WsBaseHandler<WsWorkNotReadyEvent> {
    @Override
    public void handleEvent(WsWorkNotReadyEvent event) {

    }
}
