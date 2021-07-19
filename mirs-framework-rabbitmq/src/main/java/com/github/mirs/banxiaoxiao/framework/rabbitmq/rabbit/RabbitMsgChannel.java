package com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit;

import com.github.mirs.banxiaoxiao.framework.common.util.NetworkUtil;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgChannel;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.RmqException;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.connection.ConnectionListener;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactoryUtils;
import org.springframework.amqp.rabbit.connection.RabbitResourceHolder;
import org.springframework.amqp.rabbit.connection.RabbitUtils;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Map;

/**
 * @author zcy 2019年6月19日
 */
public abstract class RabbitMsgChannel implements MsgChannel, ConnectionListener {

    public static final String CHANNEL_AUTOCREATE_KEY = "channel.autoCreate";

    private RabbitTemplate rabbitTemplate;

    private RabbitAdmin rabbitAdmin;

    private MessageConverter messageConverter;

    private String msgType;

    private Exchange exchange;

    private Queue queue;

    private Binding binding;

    private Map<String, Object> args;

    private boolean autoCreate;

    public RabbitMsgChannel() {
    }

    public RabbitMsgChannel(String msgType, RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin) {
        this(msgType, rabbitTemplate, rabbitAdmin, null);
    }

    public RabbitMsgChannel(String msgType, RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin, Map<String, Object> args) {
        this.msgType = msgType;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = rabbitAdmin;
        this.messageConverter = new SimpleMessageConverter(); // or Jackson2JsonMessageConverter
        this.rabbitTemplate.setMessageConverter(getMessageConverter());
        this.args = args;
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }

    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public static String getLocalHost() {
        return NetworkUtil.getLocalHost();
    }

    public static int getLocalProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0]).intValue();
    }

    public static String getLocalHostProcessKey() {
        return RabbitMsgChannel.getLocalHost() + "_" + RabbitMsgChannel.getLocalProcessID();
    }

    public void onConnected(Connection connection) {
        if (isAutoCreate()) {
            create();
        }
    }

    public void onDisconnect(Connection connection) {
    }

    public void onReconnected() {
        if (isAutoCreate()) {
            create();
        }
    }

    public void onReconnectStart() {
    }

    public abstract Exchange declareExchange();

    public abstract Queue declareQueue();

    public abstract Binding declareBinding();

    public String getRoutingKey(Object msg) {
        return getMsgType();
    }

    @Override
    public void create() {
        TComLogs.info("connection on created, {}", this);
        this.queue = declareQueue();
        if (!existQueue(queue)) {
            this.rabbitAdmin.declareQueue(queue);
        }
        this.exchange = declareExchange();
        this.rabbitAdmin.declareExchange(exchange);
        this.binding = declareBinding();
        this.rabbitAdmin.declareBinding(binding);
        TComLogs.info("declare queue/exchange/binding : {}/{}/{}", queue, exchange, binding);
    }

    private boolean existQueue(Queue queue) {
        Channel channel = null;
        RabbitResourceHolder resourceHolder = null;
        try {
            resourceHolder = ConnectionFactoryUtils.getTransactionalResourceHolder(this.rabbitTemplate.getConnectionFactory(), true);
            channel = resourceHolder.getChannel();
            channel.queueDeclarePassive(queue.getName());
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (resourceHolder != null) {
                ConnectionFactoryUtils.releaseResources(resourceHolder);
            }
            if (channel != null) {
                RabbitUtils.closeChannel(channel);
            }
        }
    }

    public boolean isCreate() {
        return this.binding != null;
    }

    @Override
    public void destory() {
        this.rabbitAdmin.deleteQueue(declareQueue().getName());
        this.rabbitAdmin.deleteExchange(declareExchange().getName());
    }

    @Override
    public String getMsgType() {
        return this.msgType;
    }

    public Class<?> getMsgTypeClass() {
        try {
            return Class.forName(getMsgType());
        } catch (ClassNotFoundException e) {
            throw new RmqException(e);
        }
    }

    public void clean() {
        this.rabbitAdmin.purgeQueue(declareQueue().getName(), true);
    }

    @Override
    public void send(Object msg, String routingKey, Map<String, Object> configs) {
        if (msg == null) {
            throw new NullPointerException();
        }
        // if (!msg.getClass().equals(getMsgType())) {
        // throw new ClassCastException(msg.getClass() + " cannot be converted to " + getMsgType());
        // }
        Exchange sendExchange = this.exchange;
        if (sendExchange == null) {
            sendExchange = declareExchange();
        }
        String exchange = sendExchange.getName();
        if (configs == null) {
            this.rabbitTemplate.convertAndSend(exchange, routingKey, msg);
        } else {
            this.rabbitTemplate.convertAndSend(exchange, routingKey, msg, new MessagePostProcessor() {

                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    if (configs.containsKey("ttl")) {
                        message.getMessageProperties().setExpiration(configs.get("ttl").toString());
                    }
                    return message;
                }
            });
        }
    }

    @Override
    public void send(Object msg, Map<String, Object> configs) {
        String routingKey = getRoutingKey(msg);
        send(msg, routingKey, configs);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T receive() {
        String queueName = this.queue.getName();
        Message obj = rabbitTemplate.receive(queueName);
        if (obj == null) {
            return null;
        }
        MessageConverter converter = getMessageConverter();
        if (converter != null) {
            return (T) converter.fromMessage(obj);
        } else {
            return (T) obj;
        }
    }

    protected MessageConverter getMessageConverter() {
        return this.messageConverter;
    }

    @Override
    public int hashCode() {
        if (getChannelId() == null) {
            return 0;
        } else {
            return getChannelId().hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MsgChannel)) {
            return false;
        }
        MsgChannel other = (MsgChannel) obj;
        if (other.getChannelId() == null) {
            return getChannelId() == null;
        } else {
            return other.getChannelId().equals(getChannelId());
        }
    }

    public String toString() {
        return "channel [" + getChannelId() + "," + getChannelType() + "," + getMsgType() + "]";
    }
}
