package com.voxai.cc.fs.handler;

import org.springframework.stereotype.Component;
import com.voxai.cc.fs.handler.base.BaseEventHandler;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.fs.event.FsDtmfEvent;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
@HandlerType("DTMF")
public class FsDtmfHandler extends BaseEventHandler<FsDtmfEvent> {
    @Override
    public void handleEvent(FsDtmfEvent event) {
        logger.info("{}", event.toString());
    }
}
