package com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;

/**
 * @author zcy 2019年8月22日
 */
public class RpcMsgChannel extends P2PMsgChannel {

    public RpcMsgChannel() {
        super();
    }

    public RpcMsgChannel(String msgType, RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin) {
        super(msgType, rabbitTemplate, rabbitAdmin);
    }

    public RpcMsgChannel(String msgType, RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin, Map<String, Object> args) {
        super(msgType, rabbitTemplate, rabbitAdmin, args);
    }
}
