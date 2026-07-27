package com.voxai.cc.fs.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.voxai.cc.fs.handler.base.BaseEventHandler;
import com.voxai.cc.configration.HandlerType;
import com.voxai.cc.fs.event.FsExecuteEvent;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
@HandlerType("CHANNEL_EXECUTE")
public class FsExecuteHandler extends BaseEventHandler<FsExecuteEvent> {
    private Logger logger = LoggerFactory.getLogger(FsExecuteHandler.class);

    @Override
    public void handleEvent(FsExecuteEvent event) {

    }
}
