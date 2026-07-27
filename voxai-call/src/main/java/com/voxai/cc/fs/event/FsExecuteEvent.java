package com.voxai.cc.fs.event;

import com.alibaba.fastjson.annotation.JSONField;
import com.voxai.cc.fs.event.base.FsBaseEvent;

/**
 * Created by dongjb on 2025/09/03
 */
public class FsExecuteEvent extends FsBaseEvent {

    @JSONField(name = "Application-Response")
    private String response;




}
