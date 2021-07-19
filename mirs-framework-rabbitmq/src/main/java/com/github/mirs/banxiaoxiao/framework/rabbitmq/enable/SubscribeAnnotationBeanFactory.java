package com.github.mirs.banxiaoxiao.framework.rabbitmq.enable;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgChannel;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgChannelAdmin;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgChannelType;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.annotation.P2P;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.annotation.Subscribe;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PMsgPublisherFactory;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.adapter.OldP2PClientListenerAdapter;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.consult.ConsultationDesk;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.RabbitMsgChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author erxiao 2016年11月17日
 */
public class SubscribeAnnotationBeanFactory implements BeanPostProcessor {

    private static Logger logger = LoggerFactory.getLogger(SubscribeAnnotationBeanFactory.class);

    @Autowired
    private MsgChannelAdmin rmqAdmin;

    @Autowired
    private P2PMsgPublisherFactory publisherFactory;

    @Autowired
    private ConsultationDesk consultationDesk;

    /**
     * (non-Javadoc)
     * 
     * @see BeanPostProcessor#postProcessBeforeInitialization(Object, String)
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see BeanPostProcessor#postProcessAfterInitialization(Object, String)
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Object listener = bean;
        try {
            listener = AopTargetUtils.getTarget(bean);
        } catch (Exception e) {
            logger.error("proxy error.", e);
        }
        Class<?> clazz = listener.getClass();
        if (ClassUtils.isCglibProxy(listener)) {
            clazz = listener.getClass().getSuperclass();
        }
        P2P p2p = clazz.getAnnotation(P2P.class);
        if (p2p != null) {
            registerP2PAnnotation(p2p, listener);
        }
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            try {
                Subscribe sub = method.getAnnotation(Subscribe.class);
                if (sub != null) {
                    registerSubscribeAnnotation(sub, listener, method);
                }
            } catch (Exception e) {
                throw new BeanInitializationException("Failed to register event subscriber at method " + method + " in class "
                        + bean.getClass().getName(), e);
            }
        }
        return bean;
    }

    public void registerSubscribeAnnotation(Subscribe sub, Object listener, Method method) {
        String name = method.getName();
        for (Subscribe.Type subType : sub.value()) {
            Class<?> messageType = method.getParameterTypes()[0];
            MsgChannelType type = null;
            if (subType == Subscribe.Type.P2P) {
                type = MsgChannelType.P2P;
            } else if (subType == Subscribe.Type.BROADCAST) {
                type = MsgChannelType.BROADCAST;
            } else if (subType == Subscribe.Type.QUEUE) {
                type = MsgChannelType.QUEUE;
            }
            Map<String, Object> args = new HashMap<String, Object>();
            if (sub.queueMaxLength() > 0) {
                args.put("x-max-length", sub.queueMaxLength());
            }
            if (sub.messageTtl() > 0) {
                args.put("x-message-ttl", sub.messageTtl());
            }
            args.put("durable", sub.durable());
            args.put(RabbitMsgChannel.CHANNEL_AUTOCREATE_KEY, true);
            MsgChannel channel = rmqAdmin.declareChannel(messageType.getName(), type, args);
            rmqAdmin.bindSubscribe(sub.queueCategory(), channel, listener, name);
        }
    }

    public void registerP2PAnnotation(P2P p2p, Object target) {
        com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p.P2PClientListener oldListener = (com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p.P2PClientListener) target;
        OldP2PClientListenerAdapter clientListenerAdapter = new OldP2PClientListenerAdapter(oldListener, publisherFactory, p2p.value());
        consultationDesk.addP2PClientListener(clientListenerAdapter);
    }
}
