package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgChannelType;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgPublisher;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zcy 2019年7月24日
 */
public class P2PRabbitMsgPublisherFactory extends RabbitMsgPublisherFactory implements P2PMsgPublisherFactory {

    private static Map<String, P2PMsgPublisher> p2pMsgPublishers = new HashMap<>();

    public P2PRabbitMsgPublisherFactory() {
    }

    public P2PRabbitMsgPublisherFactory(RabbitRmqAdmin rabbitRmqAdmin) {
        super(rabbitRmqAdmin);
    }

    @Override
    public synchronized MsgPublisher getMsgPublisher(MsgChannelType type, String clazz) {
        if (type == MsgChannelType.P2P) {
            P2PMsgPublisher publisher = p2pMsgPublishers.get(clazz);
            if (publisher == null) {
                publisher = createP2PMsgPublisher(clazz);
                p2pMsgPublishers.put(clazz, publisher);
            }
            return publisher;
        } else {
            return super.getMsgPublisher(type, clazz);
        }
    }

    @Override
    public synchronized P2PMsgPublisher getP2PMsgPublisher(Class<?> clazz) {
        return (P2PMsgPublisher) getMsgPublisher(MsgChannelType.P2P, clazz.getName());
    }

    protected P2PMsgPublisher createP2PMsgPublisher(String clazz) {
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put(RabbitMsgChannel.CHANNEL_AUTOCREATE_KEY, false);
        RabbitMsgChannel msgChannel = getRabbitRmqAdmin().declareChannel(clazz, MsgChannelType.P2P, configs);
        return new RabbitP2PMsgPublisher((P2PMsgChannel) msgChannel);
    }

    @Override
    public P2PMsgPublisher getP2PMsgPublisher(Class<?> clazz, P2PRouter router) {
        P2PMsgPublisher publisher = getP2PMsgPublisher(clazz);
        publisher.setP2PRouter(router);
        return publisher;
    }
}
