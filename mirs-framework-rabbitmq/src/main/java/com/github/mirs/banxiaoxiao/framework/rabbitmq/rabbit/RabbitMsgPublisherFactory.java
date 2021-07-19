package com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgChannelType;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgPublisher;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgPublisherFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zcy 2019年6月21日
 */
public class RabbitMsgPublisherFactory implements MsgPublisherFactory {

    private RabbitRmqAdmin rabbitRmqAdmin;

    private static Map<String, MsgPublisher> msgPublishers = new HashMap<>();

    public RabbitMsgPublisherFactory() {
    }

    public RabbitMsgPublisherFactory(RabbitRmqAdmin rabbitRmqAdmin) {
        this.rabbitRmqAdmin = rabbitRmqAdmin;
    }

    @Override
    public MsgPublisher getMsgPublisher(MsgChannelType type, Class<?> msgClazz) {
        return getMsgPublisher(type, msgClazz.getName());
    }

    @Override
    public synchronized MsgPublisher getMsgPublisher(MsgChannelType type, String msgClazzName) {
        String key = type + msgClazzName;
        MsgPublisher publisher = msgPublishers.get(key);
        if (publisher == null) {
            RabbitMsgChannel msgChannel = rabbitRmqAdmin.declareChannel(msgClazzName, type, null);
            publisher = new RabbitMsgPublisher(msgChannel);
            msgPublishers.put(key, publisher);
        }
        return publisher;
    }

    public RabbitRmqAdmin getRabbitRmqAdmin() {
        return rabbitRmqAdmin;
    }

    public void setRabbitRmqAdmin(RabbitRmqAdmin rabbitRmqAdmin) {
        this.rabbitRmqAdmin = rabbitRmqAdmin;
    }
}
