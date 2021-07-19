package com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
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
public class BroadcastMsgChannel extends RabbitMsgChannel {

    public BroadcastMsgChannel() {
        super();
    }

    public BroadcastMsgChannel(String msgType, RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin) {
        super(msgType, rabbitTemplate, rabbitAdmin);
    }

    public BroadcastMsgChannel(String msgType, RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin, Map<String, Object> args) {
        super(msgType, rabbitTemplate, rabbitAdmin, args);
    }

    @Override
    public MsgChannelType getChannelType() {
        return MsgChannelType.BROADCAST;
    }

    @Override
    public String getChannelId() {
        return getMsgType() + "_broadcast_" + getLocalHost() + "@" + getLocalProcessID();
    }

    protected String getExchangeName() {
        return getMsgType() + "_broadcast";
    }

    protected String getQueueName() {
        return getMsgType() + "_" + getLocalHost() + "@" + getLocalProcessID();
    }

    public void onReconnected() {
        TComLogs.info("connection reconnected, create queue [{}] again", getQueueName());
        if(isAutoCreate()) {
            super.create();
        }
    }

    @Override
    public TopicExchange declareExchange() {
        String exchangeName = getExchangeName();
        TopicExchange exchange = new TopicExchange(exchangeName, false, true, getArgs());
        return exchange;
    }

    @Override
    public Binding declareBinding() {
        String routingKey = getRoutingKey(null);
        return BindingBuilder.bind(declareQueue()).to(new TopicExchange(getExchangeName())).with(routingKey);
    }

    @Override
    public Queue declareQueue() {
        String queueName = getQueueName();
        Queue queue = new Queue(queueName, false, false, true, getArgs());
        return queue;
    }

    @Override
    public String getRoutingKey(Object msg) {
        return getMsgType();
    }
}
