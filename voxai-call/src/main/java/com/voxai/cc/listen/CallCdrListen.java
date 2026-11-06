package com.voxai.cc.listen;

import com.alibaba.fastjson.JSON;
import com.voxai.cc.cdr.CdrCompletion;
import com.voxai.cc.cdr.CdrPersistenceService;
import com.voxai.core.constant.Constant;
import com.voxai.core.entity.*;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Collections;

/** Exceptions intentionally propagate so the container can retry a rolled-back transaction. */
@Component
public class CallCdrListen {
    @Autowired private CdrPersistenceService persistence;

    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = Constant.CALL_LOG_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = Constant.DEVOCE_KEY, value = @Queue(Constant.CALL_DEVICE_QUEUE)))
    public void listenCallDevice(String payload) {
        persistence.saveDevice(JSON.parseObject(payload, CallDevice.class));
    }

    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = Constant.CALL_LOG_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = Constant.DETAIL_KEY, value = @Queue(Constant.CALL_DETAIL_QUEUE)))
    public void listenCallDetailQueue(String payload) {
        persistence.saveDetails(Collections.singletonList(JSON.parseObject(payload, CallDetail.class)));
    }

    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = Constant.CALL_LOG_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = Constant.CALLLOG_KEY, value = @Queue(Constant.CALL_LOG_QUEUE)))
    public void listenCallLog(String payload) {
        persistence.saveLog(JSON.parseObject(payload, CallLog.class));
    }

    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(value = Constant.CALL_LOG_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = Constant.CALL_COMPLETE_KEY, value = @Queue(Constant.CALL_COMPLETE_QUEUE)))
    public void listenCompletion(String payload) {
        persistence.complete(JSON.parseObject(payload, CdrCompletion.class));
    }
}
