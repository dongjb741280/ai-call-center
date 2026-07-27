package com.voxai.cc.fs.handler;

import org.springframework.stereotype.Component;
import com.voxai.cc.fs.handler.base.BaseEventHandler;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.fs.event.FsDtmfEvent;

/**
 * Created by dongjb on 2025/09/03
 * <p>
 * 按键收号
 */

@Component
@HandlerType("DTMF")
public class FsDtmfHandler extends BaseEventHandler<FsDtmfEvent> {
    @Override
    public void handleEvent(FsDtmfEvent event) {
        logger.info("{}", event.toString());
    }
}
