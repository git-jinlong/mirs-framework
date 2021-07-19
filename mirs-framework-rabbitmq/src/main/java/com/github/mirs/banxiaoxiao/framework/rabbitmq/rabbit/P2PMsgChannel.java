package com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgChannelType;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.RmqException;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;

/**
 * @author zcy 2019年6月19日
 */
public class P2PMsgChannel extends BroadcastMsgChannel {

    private String targetHost;

    public P2PMsgChannel() {
        super();
    }

    public P2PMsgChannel(String msgType, RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin) {
        super(msgType, rabbitTemplate, rabbitAdmin);
    }

    public P2PMsgChannel(String msgType, RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin, Map<String, Object> args) {
        super(msgType, rabbitTemplate, rabbitAdmin, args);
    }

    public String getTargetHost() {
        return targetHost;
    }

    public void setTargetHost(String targetHost) {
        this.targetHost = targetHost;
    }

    @Override
    public MsgChannelType getChannelType() {
        return MsgChannelType.P2P;
    }

    private String getHost() {
        return StringUtil.isBlank(this.targetHost) ? getLocalHostProcessKey() : this.targetHost;
    }

    @Override
    public String getChannelId() {
        return getMsgType() + "_p2p_" + getHost();
    }

    @Override
    public String getRoutingKey(Object msg) {
        return getMsgType() + "_" + getHost();
    }

    @Override
    public void send(Object msg, Map<String, Object> configs) {
        if (StringUtil.isBlank(this.targetHost)) {
            throw new RmqException("p2p channel must set targetHost");
        }
        super.send(msg, configs);
    }
}
