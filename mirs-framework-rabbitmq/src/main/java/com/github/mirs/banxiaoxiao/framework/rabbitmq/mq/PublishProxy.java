package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.EventErrorCode;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author zcy 2018年9月11日
 */
public class PublishProxy {

    private RmqChannel channel;

    private RabbitTemplate template;

    public PublishProxy(RmqChannel connection) {
        this.channel = connection;
        template = new RabbitTemplate(this.channel.getConnectionFactory());
    }

    public int publish(Object event) {
        String eventName = event.getClass().getName();
        template.convertAndSend(eventName, eventName, event);
        return EventErrorCode.SUCCEED;
    }

    public int publish(String eventName,Object event) {
        template.convertAndSend(eventName, eventName, event);
        return EventErrorCode.SUCCEED;
    }

    public int publish(Object event, int ttl) {
        String eventName = event.getClass().getName();
        template.convertAndSend(eventName, eventName, event, new MessagePostProcessor() {

            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration(ttl + "");
                return message;
            }
        });
        return EventErrorCode.SUCCEED;
    }

    public int broadcast(Object event) {
        String eventName = event.getClass().getName();
        template.convertAndSend(eventName + "_broadcast", eventName, event);
        return EventErrorCode.SUCCEED;
    }

    public int broadcast(String eventName,Object event) {
        template.convertAndSend(eventName + "_broadcast", eventName, event);
        return EventErrorCode.SUCCEED;
    }
    
    public int broadcast(Object event, int ttl) {
        String eventName = event.getClass().getName();
        template.convertAndSend(eventName + "_broadcast", eventName, event, new MessagePostProcessor() {

            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration(ttl + "");
                return message;
            }
        });
        return EventErrorCode.SUCCEED;
    }
}
