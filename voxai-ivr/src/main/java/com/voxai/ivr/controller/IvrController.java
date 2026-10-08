package com.voxai.ivr.controller;

import com.voxai.ivr.flow.IvrExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * IVR 引擎入口。
 */
@RestController
@RequestMapping("index")
public class IvrController {
    private static final Logger logger = LoggerFactory.getLogger(IvrController.class);

    @Autowired
    private IvrExecutor ivrExecutor;

    @GetMapping("start")
    public String start(@RequestParam Long callId,
                        @RequestParam String deviceId,
                        @RequestParam Long ivrId,
                        @RequestParam(required = false) String mediaHost) {
        logger.info("ivr start callId:{}, deviceId:{}, ivrId:{}, mediaHost:{}", callId, deviceId, ivrId, mediaHost);
        ivrExecutor.start(callId, deviceId, ivrId, mediaHost);
        return "ok";
    }

    @GetMapping("health")
    public String health() {
        return "up";
    }
}
