package com.voxai.web.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.voxai.cc.cache.CacheService;
import com.voxai.cc.command.GroupHandler;
import com.voxai.cc.fs.FsListen;
import com.voxai.cc.service.AgentService;
import com.voxai.cc.service.CallCdrService;
import com.voxai.cc.websocket.handler.WsLogoutHandler;
import com.voxai.cc.websocket.handler.WsNotReadyHandler;
import com.voxai.cc.websocket.handler.WsReadyHandler;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class BaseController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected CallCdrService callCdrService;

    @Autowired
    protected CacheService cacheService;

    @Autowired
    protected AgentService agentService;

    @Autowired
    protected GroupHandler groupHandler;

    @Autowired
    protected WsReadyHandler readyHandler;

    @Autowired
    protected WsNotReadyHandler notReadyHandler;

    @Autowired
    protected WsLogoutHandler logoutHandler;

    @Autowired
    protected FsListen fsListen;

    @Value("${agent.token.key:ToIV23TaievkWwZl}")
    protected String tokenKey;


}
