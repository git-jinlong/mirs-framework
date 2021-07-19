package com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.listener;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.RmqProperty;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.RabbitMsgChannel;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zcy 2019年6月21日
 */
public class PriorityMessageListenerContainer extends SimpleMessageListenerContainer {

    private List<RabbitMsgChannel> channels;

    private Map<String, EventHandler> handlers;

    private int priority;

    private RmqProperty property;

    private ThreadPoolExecutor threadPoolExecutor;
    
    private MessageConverter messageConverter;

    public PriorityMessageListenerContainer(RmqProperty property, int priority, ConnectionFactory connectionFactory,
            ThreadPoolExecutor threadPoolExecutor) {
        this.priority = priority;
        this.property = property;
        this.handlers = new HashMap<String, EventHandler>();
        this.channels = new ArrayList<RabbitMsgChannel>();
        this.messageConverter = new SimpleMessageConverter(); //or Jackson2JsonMessageConverter();
        this.threadPoolExecutor = threadPoolExecutor;
        setConnectionFactory(connectionFactory);
    }

    @Override
    protected void doInitialize() {
        String[] queues = getQueueNames();
        int actualPrefetchCount = property.getPrefetchCount() > 1 ? property.getPrefetchCount() : 1;
        MessageListenerAdapter adapter = new MessageListenerAdapter(this, this.messageConverter);
        setMessageListener(adapter);
        setPrefetchCount(actualPrefetchCount);
        setConcurrentConsumers(1);
        setQueueNames(queues);
    }

    public void handleMessage(final Object event) {
        threadPoolExecutor.execute(new RunnablePriority(priority) {

            @Override
            public void run() {
                String eventName = null;
                boolean isJsonType = false;
                String message = null;
                if (event instanceof String) {
                    String eventJson = String.valueOf(event);
                    if (eventJson.startsWith("@", 0)) {
                        String[] split = eventJson.split("##");
                        eventName = split[0].substring(1);
                        message = split[1];
                        isJsonType = true;
                    } else {
                        eventName = event.getClass().getName();
                    }
                } else {
                    eventName = event.getClass().getName();
                }
                EventHandler handler = handlers.get(eventName);
                if (handler != null) {
                    try {
                        handler.handle(eventName, isJsonType, message, event);
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
            }
        });
    }

    public void bindChannelSubscribe(RabbitMsgChannel channel, Object subscriber, String methodName) throws NoSuchMethodException, SecurityException {
        if (!this.channels.contains(channel)) {
            this.channels.add(channel);
            Method method = subscriber.getClass().getMethod(methodName, channel.getMsgTypeClass());
            String eventName = channel.getMsgType();
            EventHandler handler = new EventHandler(subscriber, method);
            this.handlers.put(eventName, handler);
        }
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public List<RabbitMsgChannel> getChannels() {
        return channels;
    }

    public String[] getQueueNames() {
        if (this.channels == null) {
            return null;
        } else {
            String[] queueNames = new String[this.channels.size()];
            for (int i = 0; i < this.channels.size(); i++) {
                queueNames[i] = this.channels.get(i).declareQueue().getName();
            }
            return queueNames;
        }
    }

}
