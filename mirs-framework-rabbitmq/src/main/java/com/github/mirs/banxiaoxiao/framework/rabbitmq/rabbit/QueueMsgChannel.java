package com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgChannelType;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;

/**
 * @author zcy 2019年6月19日
 */
public class QueueMsgChannel extends RabbitMsgChannel {

    public QueueMsgChannel() {
        super();
    }

    public QueueMsgChannel(String msgType, RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin) {
        super(msgType, rabbitTemplate, rabbitAdmin);
    }

    public QueueMsgChannel(String msgType, RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin, Map<String, Object> args) {
        super(msgType, rabbitTemplate, rabbitAdmin, args);
    }

    @Override
    public MsgChannelType getChannelType() {
        return MsgChannelType.QUEUE;
    }

    @Override
    public String getChannelId() {
        return getMsgType();
    }

    @Override
    public TopicExchange declareExchange() {
        return new TopicExchange(getMsgType(), false, true, getArgs());
    }

    @Override
    public Queue declareQueue() {
        boolean durable = true;
        if (getArgs() != null && getArgs().containsKey("durable")) {
            durable = (boolean) getArgs().get("durable");
        }
        return new Queue(getMsgType(), durable, false, false, getArgs());
    }

    @Override
    public Binding declareBinding() {
        return BindingBuilder.bind(declareQueue()).to(declareExchange()).with(getMsgType());
    }
}
