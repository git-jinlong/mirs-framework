package com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.listener;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.BlockingQueueConsumer;
import org.springframework.amqp.rabbit.support.ActiveObjectCounter;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;

import java.util.Map;


public class RmqBlockingQueueConsumer extends BlockingQueueConsumer {

    public RmqBlockingQueueConsumer(ConnectionFactory connectionFactory, MessagePropertiesConverter messagePropertiesConverter,
                                    ActiveObjectCounter<BlockingQueueConsumer> activeObjectCounter, AcknowledgeMode acknowledgeMode, boolean transactional,
                                    int prefetchCount, boolean defaultRequeueRejected, Map<String, Object> consumerArgs, boolean noLocal, boolean exclusive, String[] queues) {
        super(connectionFactory, messagePropertiesConverter, activeObjectCounter, acknowledgeMode, transactional, prefetchCount, defaultRequeueRejected,
                consumerArgs, noLocal, exclusive, queues);
    }

    @Override
    public boolean hasDelivery() {
        return super.hasDelivery();
    }

    @Override
    public boolean cancelled() {
        return super.cancelled();
    }
}
