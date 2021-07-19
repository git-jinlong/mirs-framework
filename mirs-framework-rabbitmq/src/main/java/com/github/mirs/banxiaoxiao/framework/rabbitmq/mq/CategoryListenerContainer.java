package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

/**
 * @author zcy 2018年11月5日
 */
public class CategoryListenerContainer {

    private static Logger logger = LoggerFactory.getLogger(CategoryListenerContainer.class);

    private int priority;

    private SimpleMessageListenerContainer container;

    private MessageHander messageHander;

    public CategoryListenerContainer(int priority, RmqChannel channel, String[] queueName, MessageHander messageHander, int prefetchCount) {
        this.priority = priority;
        this.container = new SimpleMessageListenerContainer(channel.getConnectionFactory());
        if (channel.getProperty().getPossibleAuthenticationFailure() != null) {
            this.container.setPossibleAuthenticationFailureFatal(channel.getProperty().getPossibleAuthenticationFailure());
        }
        this.messageHander = messageHander;
        this.container.setPrefetchCount(prefetchCount);
        this.container.setConcurrentConsumers(1);
        this.container.setAutoDeclare(false);
        this.container.setMissingQueuesFatal(true);
        this.container.setDeclarationRetries(0);
        this.container.setFailedDeclarationRetryInterval(-1);
        this.container.setQueueNames(queueName);
        MessageListenerAdapter adapter = new MessageListenerAdapter(this);
        this.container.setMessageListener(adapter);
    }

    public void start() {
        if (!this.container.isRunning()) {
            this.container.start();
            logger.debug("priority {} queue listener container started", priority);
        }
    }
    
    public void stop() {
        this.container.stop();
    }

    public void handleMessage(final Object event) {
        this.messageHander.handleMessage(event, priority);
    }

    public void addSubscribeQueueName(String queueName) {
        this.container.addQueueNames(queueName);
    }

    public void removeSubscribeQueueName(String queueName) {
        this.container.removeQueueNames(queueName);
    }

    public void setRabbitAdmin(RabbitAdmin rabbitAdmin) {
        this.container.setAmqpAdmin(rabbitAdmin);
    }
}
