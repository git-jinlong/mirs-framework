package com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.core.spring.SpringContextHolder;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.*;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.connection.ConnectionListener;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.connection.RecoveryCachingConnectionFactory;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.listener.PriorityBlockingQueue;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.listener.PriorityMessageListenerContainer;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zcy 2019年7月24日
 */
public class RabbitRmqAdmin implements MsgChannelAdmin, ApplicationListener<ContextRefreshedEvent>, ConnectionListener {

    private RabbitTemplate rabbitTemplate;

    private RabbitAdmin rabbitAdmin;

    private RmqProperty property;

    private ThreadPoolExecutor threadPoolExecutor;

    private Map<Integer, PriorityMessageListenerContainer> messageListenerContainers = new HashMap<Integer, PriorityMessageListenerContainer>();

    private List<RabbitMsgChannel> channels;

    private PriorityBlockingQueue<Runnable> subscribeMsgQeueu;

    private AtomicBoolean started = new AtomicBoolean(false);

    private AtomicBoolean connected = new AtomicBoolean(false);

    public RabbitRmqAdmin(RmqProperty property, RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = rabbitAdmin;
        this.property = property;
        this.channels = new ArrayList<>();
        int threadNum = this.property.getThreads();
        if (threadNum == 0) {
            threadNum = Runtime.getRuntime().availableProcessors() * 3;
        }
        if (this.rabbitTemplate.getConnectionFactory() instanceof RecoveryCachingConnectionFactory) {
            ((RecoveryCachingConnectionFactory) this.rabbitTemplate.getConnectionFactory()).addListener(this);
        }
        this.rabbitTemplate.setChannelTransacted(property.isChannelTransacted());
        this.subscribeMsgQeueu = new PriorityBlockingQueue<Runnable>(new ArrayList<Integer>(), threadNum);
        this.threadPoolExecutor = new ThreadPoolExecutor(threadNum, threadNum, 5, TimeUnit.MINUTES, subscribeMsgQeueu,
                new RejectedExecutionHandler() {

                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        try {
                            subscribeMsgQeueu.put((Runnable) r);
                        } catch (InterruptedException e) {
                            throw new RejectedExecutionException();
                        }
                    }
                });
    }

    @Override
    public MsgChannel declareChannel(Class<?> msgClazz, MsgChannelType type, Map<String, Object> configs) {
        return declareChannel(msgClazz.getName(), type, configs);
    }

    @Override
    public synchronized RabbitMsgChannel declareChannel(String msgClazzName, MsgChannelType type, Map<String, Object> configs) {
        RabbitMsgChannel channel = findRabbitMsgChannel(msgClazzName, type);
        if (channel != null) {
            return channel;
        }
        if (type == MsgChannelType.QUEUE) {
            channel = new QueueMsgChannel(msgClazzName, rabbitTemplate, rabbitAdmin, configs);
        } else if (type == MsgChannelType.BROADCAST) {
            channel = new BroadcastMsgChannel(msgClazzName, rabbitTemplate, rabbitAdmin, configs);
        } else if (type == MsgChannelType.P2P) {
            channel = new P2PMsgChannel(msgClazzName, rabbitTemplate, rabbitAdmin, configs);
        } else if (type == MsgChannelType.RPC) {
            channel = new RpcMsgChannel(msgClazzName, rabbitTemplate, rabbitAdmin, configs);
        } else {
            throw new UnsupportedOperationException("not supported channel " + type);
        }
        boolean autoCreate = type == MsgChannelType.QUEUE ? true : false;
        if (configs != null) {
            Object v = configs.get(RabbitMsgChannel.CHANNEL_AUTOCREATE_KEY);
            if (v != null) {
                autoCreate = Boolean.parseBoolean(v.toString());
                configs.remove(RabbitMsgChannel.CHANNEL_AUTOCREATE_KEY);
            }
        }
        channel.setAutoCreate(autoCreate);
        if (autoCreate && connected.get()) {
            channel.create();
        }
        this.channels.add(channel);
        return channel;
    }

    private RabbitMsgChannel findRabbitMsgChannel(String msgClazzName, MsgChannelType type) {
        if (this.channels != null) {
            for (RabbitMsgChannel channel : this.channels) {
                if (channel.getMsgType().equals(msgClazzName) && channel.getChannelType() == type) {
                    return channel;
                }
            }
        }
        return null;
    }

    @Override
    public synchronized void bindSubscribe(int priority, MsgChannel channel, Object subscriber, String method) {
        TComLogs.debug("bind subscribe priority={}, channel={}, subscriber=[{},{}]", priority, channel, subscriber, method);
        if (!(channel instanceof RabbitMsgChannel)) {
            throw new UnsupportedOperationException("not support channel " + channel);
        }
        PriorityMessageListenerContainer container = messageListenerContainers.get(priority);
        if (container == null) {
            container = new PriorityMessageListenerContainer(this.property, priority, this.rabbitTemplate.getConnectionFactory(),
                    this.threadPoolExecutor);
            container.setApplicationContext(SpringContextHolder.get());
            this.messageListenerContainers.put(priority, container);
        }
        try {
            container.bindChannelSubscribe((RabbitMsgChannel) channel, subscriber, method);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RmqException("" + channel, e);
        }
    }

    public void startSubscribe() {
        if (started.get()) {
            return;
        }
        this.subscribeMsgQeueu.resetPrioritys(new ArrayList<Integer>(this.messageListenerContainers.keySet()));
        for (PriorityMessageListenerContainer container : this.messageListenerContainers.values()) {
            container.start();
        }
        started.set(true);
    }

    public RabbitTemplate getRabbitTemplate() {
        return rabbitTemplate;
    }

    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public RabbitAdmin getRabbitAdmin() {
        return rabbitAdmin;
    }

    public void setRabbitAdmin(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    @Override
    public void onConnected(Connection connection) {
        connected.set(true);
        TComLogs.info("onConnected channel size {}", channels.size());
        for (RabbitMsgChannel channel : channels) {
            channel.onConnected(connection);
        }
    }

    @Override
    public void onDisconnect(Connection connection) {
        connected.set(false);
        for (RabbitMsgChannel channel : channels) {
            channel.onDisconnect(connection);
        }
    }

    @Override
    public void onReconnected() {
        TComLogs.info("onReconnected channel size {}", channels.size());
        for (RabbitMsgChannel channel : channels) {
            channel.onReconnected();
        }
    }

    @Override
    public void onReconnectStart() {
        for (RabbitMsgChannel channel : channels) {
            channel.onReconnectStart();
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        startSubscribe();
    }

    @Override
    public List<MsgChannel> getMsgChannels() {
        List<MsgChannel> msgChannels = new ArrayList<>();
        if (channels != null) {
            for (RabbitMsgChannel channel : channels) {
                msgChannels.add(channel);
            }
        }
        return msgChannels;
    }

    @Override
    public List<MsgChannel> getSubscribeMsgChannels() {
        List<MsgChannel> msgChannels = new ArrayList<>();
        for (PriorityMessageListenerContainer c : this.messageListenerContainers.values()) {
            if (c.getChannels() != null) {
                for (MsgChannel channel : c.getChannels()) {
                    if (!msgChannels.contains(channel)) {
                        msgChannels.add(channel);
                    }
                }
            }
        }
        return msgChannels;
    }
}
