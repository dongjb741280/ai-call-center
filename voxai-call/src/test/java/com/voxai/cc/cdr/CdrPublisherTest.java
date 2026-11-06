package com.voxai.cc.cdr;

import com.voxai.core.constant.Constant;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CdrPublisherTest {
    @Test void acknowledgedRoutableMessageIsPersistent() { assertTrue(publish(true, false, false)); }
    @Test void nackOrReturnOrBrokerFailureTriggersDirectFallback() {
        assertFalse(publish(false, false, false));
        assertFalse(publish(true, true, false));
        assertFalse(publish(true, false, true));
    }
    @Test void missingConfirmOrReturnSupportTriggersDirectFallback() {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        CachingConnectionFactory factory = mock(CachingConnectionFactory.class);
        when(rabbit.getConnectionFactory()).thenReturn(factory);
        CdrPublisher publisher = new CdrPublisher();
        ReflectionTestUtils.setField(publisher, "rabbit", rabbit);
        assertFalse(publisher.publish(Constant.CALL_COMPLETE_KEY, new CdrCompletion()));
        when(factory.isPublisherConfirms()).thenReturn(true);
        assertFalse(publisher.publish(Constant.CALL_COMPLETE_KEY, new CdrCompletion()));
        verify(rabbit, never()).convertAndSend(anyString(), anyString(), any(Object.class), any(MessagePostProcessor.class), any(CorrelationData.class));
    }
    private boolean publish(boolean ack, boolean returned, boolean error) {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        CachingConnectionFactory factory = mock(CachingConnectionFactory.class);
        when(rabbit.getConnectionFactory()).thenReturn(factory);
        when(factory.isPublisherConfirms()).thenReturn(true);
        when(factory.isPublisherReturns()).thenReturn(true);
        doAnswer(invocation -> {
            if (error) { throw new IllegalStateException("broker unavailable"); }
            MessagePostProcessor processor = invocation.getArgument(3);
            Message message = processor.postProcessMessage(new Message(new byte[0], new MessageProperties()));
            assertEquals(MessageDeliveryMode.PERSISTENT, message.getMessageProperties().getDeliveryMode());
            CorrelationData data = invocation.getArgument(4);
            if (returned) { data.setReturnedMessage(message); }
            data.getFuture().set(new CorrelationData.Confirm(ack, "test"));
            return null;
        }).when(rabbit).convertAndSend(anyString(), anyString(), any(Object.class), any(MessagePostProcessor.class), any(CorrelationData.class));
        CdrPublisher publisher = new CdrPublisher();
        ReflectionTestUtils.setField(publisher, "rabbit", rabbit);
        return publisher.publish(Constant.CALL_COMPLETE_KEY, new CdrCompletion());
    }
}
