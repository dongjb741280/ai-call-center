package com.voxai.cc.websocket.handler;

import org.springframework.stereotype.Component;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.websocket.handler.base.WsBaseHandler;
import com.voxai.cc.websocket.event.WsWorkNotReadyEvent;

/**
 * Create by dongjb on 2025/09/03
 * <p>
 * 自定义忙碌状态
 */
@Component
@HandlerType("WS_WORK_NOT_READY")
public class WsWorkNotReadyHandler extends WsBaseHandler<WsWorkNotReadyEvent> {
    @Override
    public void handleEvent(WsWorkNotReadyEvent event) {

    }
}
