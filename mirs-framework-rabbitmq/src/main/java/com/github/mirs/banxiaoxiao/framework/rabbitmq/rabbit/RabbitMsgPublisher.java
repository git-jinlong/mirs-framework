package com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgPublisher;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zcy 2019年6月25日
 */
public class RabbitMsgPublisher implements MsgPublisher {

    protected RabbitMsgChannel rabbitChannle;

    public RabbitMsgPublisher(RabbitMsgChannel rabbitChannle) {
        this.rabbitChannle = rabbitChannle;
    }

    @Override
    public void publish(Object msg) {
        rabbitChannle.send(msg, null);
    }

    @Override
    public void publish(Object msg, int ttl) {
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put("ttl", ttl);
        rabbitChannle.send(msg, configs);
    }

    public void setRabbitChannle(RabbitMsgChannel rabbitChannle) {
        this.rabbitChannle = rabbitChannle;
    }
}
