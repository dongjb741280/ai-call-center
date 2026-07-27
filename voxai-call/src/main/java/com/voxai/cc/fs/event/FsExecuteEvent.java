package com.voxai.cc.fs.event;

import com.alibaba.fastjson.annotation.JSONField;
import com.voxai.cc.fs.event.base.FsBaseEvent;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class FsExecuteEvent extends FsBaseEvent {

    @JSONField(name = "Application-Response")
    private String response;




}
