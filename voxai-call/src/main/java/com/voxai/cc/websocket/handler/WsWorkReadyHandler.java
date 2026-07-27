package com.voxai.cc.websocket.handler;

import org.springframework.stereotype.Component;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.websocket.handler.base.WsBaseHandler;
import com.voxai.cc.websocket.event.WsWorkReadyEvent;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
@HandlerType("WS_WORK_READY")
public class WsWorkReadyHandler extends WsBaseHandler<WsWorkReadyEvent> {
    @Override
    public void handleEvent(WsWorkReadyEvent event) {
        logger.info("{}", event.toString());
    }
}
