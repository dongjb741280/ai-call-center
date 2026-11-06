package com.voxai.cc.cdr;

import com.alibaba.fastjson.JSON;
import com.voxai.core.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
public class CdrPublisher {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired private RabbitTemplate rabbit;

    /** Unconfirmed or unroutable sends fall back to the same idempotent direct writer. */
    public boolean publish(String key, Object payload) {
        if (!(rabbit.getConnectionFactory() instanceof CachingConnectionFactory)
                || (!((CachingConnectionFactory) rabbit.getConnectionFactory()).isPublisherConfirms()
                || !((CachingConnectionFactory) rabbit.getConnectionFactory()).isPublisherReturns())) {
            logger.warn("CDR publisher confirms unavailable; falling back to direct persistence");
            return false;
        }
        try {
            rabbit.setMandatory(true);
            CorrelationData correlation = new CorrelationData();
            rabbit.convertAndSend(Constant.CALL_LOG_EXCHANGE, key, JSON.toJSONString(payload), message -> {
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            }, correlation);
            return correlation.getFuture().get(5, TimeUnit.SECONDS).isAck()
                    && correlation.getReturnedMessage() == null;
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception error) {
            logger.warn("CDR publish failed; falling back to direct persistence", error);
            return false;
        }
    }
}
