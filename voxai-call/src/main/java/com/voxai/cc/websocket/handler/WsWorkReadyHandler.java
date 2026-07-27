package com.voxai.cc.websocket.handler;

import org.springframework.stereotype.Component;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.websocket.handler.base.WsBaseHandler;
import com.voxai.cc.websocket.event.WsWorkReadyEvent;

/**
 * Create by dongjb on 2025/09/03
 * <p>
 * 上班之后的状态
 */
@Component
@HandlerType("WS_WORK_READY")
public class WsWorkReadyHandler extends WsBaseHandler<WsWorkReadyEvent> {
    @Override
    public void handleEvent(WsWorkReadyEvent event) {
        logger.info("{}", event.toString());
    }
}
