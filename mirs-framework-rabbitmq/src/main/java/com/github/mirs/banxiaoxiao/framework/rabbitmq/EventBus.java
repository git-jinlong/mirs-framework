package com.github.mirs.banxiaoxiao.framework.rabbitmq;

import com.github.mirs.banxiaoxiao.framework.common.util.JsonUtils;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.RmqChannel;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.SubscribeProxys;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author erxiao 2017年2月8日
 */
@Deprecated
public final class EventBus {

    private static MsgPublisherFactory publisherFactory;

    private static MsgChannelAdmin rmqAdmin;

    private static Map<String, MsgPublisher> msgPublishers = new HashMap<>();

    public static void init(MsgPublisherFactory publisherFactory, MsgChannelAdmin rmqAdmin) {
        EventBus.publisherFactory = publisherFactory;
    }

    public static void init(com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.RmqProperty p) {
        throw new UnsupportedOperationException();
    }

    public static RmqChannel getChannel() {
        throw new UnsupportedOperationException();
    }

    public static SubscribeProxys getSubscribeProxy() {
        throw new UnsupportedOperationException();
    }

    public static void destroy() {
        throw new UnsupportedOperationException();
    }

    public static synchronized MsgPublisher getMsgPublisher(MsgChannelType type, Class<?> clazz) {
        String key = type + clazz.getName();
        MsgPublisher publisher = msgPublishers.get(key);
        if (publisher == null) {
            publisher = publisherFactory.getMsgPublisher(type, clazz);
            msgPublishers.put(key, publisher);
        }
        return publisher;
    }

    public static synchronized MsgPublisher getMsgPublisher(MsgChannelType type, String clazzName) {
        String key = type + clazzName;
        MsgPublisher publisher = msgPublishers.get(key);
        if (publisher == null) {
            publisher = publisherFactory.getMsgPublisher(type, clazzName);
            msgPublishers.put(key, publisher);
        }
        return publisher;
    }

    /**
     * 订阅事件
     *
     * @param subscriber
     * @return
     */
    public static int registerListener(Object subscriber) {
        throw new UnsupportedOperationException();
    }

    public static int startSubscribe() {
        throw new UnsupportedOperationException();
    }

    /**
     * 发布异步事件
     *
     * @param event
     */
    public static int publish(Object event) {
        if (event == null) {
            return EventErrorCode.INVALID_MESSAGE;
        }
        MsgPublisher publisher = getMsgPublisher(MsgChannelType.QUEUE, event.getClass());
        publisher.publish(event);
        return EventErrorCode.SUCCEED;
    }

    /**
     * @param event
     * @param ttl   超时时间，单位毫秒
     * @return
     */
    public static int publish(Object event, int ttl) {
        if (event == null) {
            return EventErrorCode.INVALID_MESSAGE;
        }
        MsgPublisher publisher = getMsgPublisher(MsgChannelType.QUEUE, event.getClass());
        publisher.publish(event, ttl);
        return EventErrorCode.SUCCEED;
    }

    public static int publishJson(Object event) {
        if (event == null) {
            return EventErrorCode.INVALID_MESSAGE;
        }
        String eventName = event.getClass().getName();
        MsgPublisher publisher = getMsgPublisher(MsgChannelType.QUEUE, event.getClass());
        publisher.publish("@".concat(eventName).concat("##").concat(JsonUtils.toJson(event)));
        return EventErrorCode.SUCCEED;
    }

    public static int publishJson(String eventName, String event) {
        if (StringUtils.isEmpty(eventName)) {
            return EventErrorCode.INVALID_MESSAGE;
        }
        if (StringUtils.isEmpty(event)) {
            return EventErrorCode.INVALID_MESSAGE;
        }
        MsgPublisher publisher = getMsgPublisher(MsgChannelType.QUEUE, eventName);
        publisher.publish("@".concat(eventName).concat("##").concat(event));
        return EventErrorCode.SUCCEED;
    }

    public static int clean(Class<?> clazz, boolean noWait) {
        MsgChannel channel = rmqAdmin.declareChannel(clazz.getName(), MsgChannelType.QUEUE, null);
        channel.clean();
        return EventErrorCode.SUCCEED;
    }

    public static int clean(String queueName, boolean noWait) {
        throw new UnsupportedOperationException();
    }

    public static int broadcast(Object event) {
        if (event == null) {
            return EventErrorCode.INVALID_MESSAGE;
        }
        MsgPublisher publisher = getMsgPublisher(MsgChannelType.BROADCAST, event.getClass());
        publisher.publish(event);
        return EventErrorCode.SUCCEED;
    }

    public static int broadcastJson(Object event) {
        if (event == null) {
            return EventErrorCode.INVALID_MESSAGE;
        }
        String eventName = event.getClass().getName();
        MsgPublisher publisher = getMsgPublisher(MsgChannelType.BROADCAST, event.getClass());
        publisher.publish("@".concat(eventName).concat("##").concat(JsonUtils.toJson(event)));
        return EventErrorCode.SUCCEED;
    }

    public static int broadcastJson(String eventName, String event) {
        if (StringUtils.isEmpty(eventName)) {
            return EventErrorCode.INVALID_MESSAGE;
        }
        if (StringUtils.isEmpty(event)) {
            return EventErrorCode.INVALID_MESSAGE;
        }
        MsgPublisher publisher = getMsgPublisher(MsgChannelType.BROADCAST, eventName);
        publisher.publish("@".concat(eventName).concat("##").concat(event));
        return EventErrorCode.SUCCEED;
    }

    public static int broadcast(Object event, int ttl) {
        if (event == null) {
            return EventErrorCode.INVALID_MESSAGE;
        }
        MsgPublisher publisher = getMsgPublisher(MsgChannelType.BROADCAST, event.getClass());
        publisher.publish(event, ttl);
        return EventErrorCode.SUCCEED;
    }
}
