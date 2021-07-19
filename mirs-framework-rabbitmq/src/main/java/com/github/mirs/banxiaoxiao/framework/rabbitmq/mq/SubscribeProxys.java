package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq;

import com.github.mirs.banxiaoxiao.framework.common.util.ReflectionUtils;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.EventErrorCode;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.annotation.P2P;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.annotation.Subscribe;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.annotation.Subscribe.Type;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.enable.AopTargetUtils;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p.P2PClientDesk;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p.P2PServerDesk;
import com.google.common.collect.Maps;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.RecoveryListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.util.ClassUtils;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author zcy 2018年9月11日
 */
public class SubscribeProxys implements MessageHander {

    private static Logger logger = LoggerFactory.getLogger(SubscribeProxys.class);

    private RmqChannel channel;

    private RabbitAdmin admin;

    private Map<String, EventHandler> handlers;

    private P2PServerDesk p2pServerDesk;

    private P2PClientDesk p2pClientDesk;

    private Map<Integer, List<String>> queueCategorys = new ConcurrentHashMap<Integer, List<String>>();

    private Map<Integer, Integer> queueCategoryPrefetchCounts = new ConcurrentHashMap<Integer, Integer>();

    private List<Object> proxyObjectList = new ArrayList<Object>();

    private Map<Integer, CategoryListenerContainer> containers;

    private ThreadPoolExecutor threadPoolExecutor;

    private BlockingQueue<Runnable> subscribeMsgQeueu;

    public SubscribeProxys(final RmqChannel channel) {
        this.channel = channel;
        this.admin = new RabbitAdmin(this.channel.getConnectionFactory());
        this.handlers = Maps.newConcurrentMap();
        this.containers = new HashMap<Integer, CategoryListenerContainer>();
        this.channel.getConnectionFactory().setRecoveryListener(new RecoveryListener() {

            @Override
            public void handleRecoveryStarted(Recoverable recoverable) {
                logger.info("rabbit mq {} {} recovery started", channel.getProperty().getHost(), channel.getProperty().getAddresses());
            }

            @Override
            public void handleRecovery(Recoverable recoverable) {
                logger.info("rabbit mq {} {} recovery", channel.getProperty().getHost(), channel.getProperty().getAddresses());
                for (CategoryListenerContainer container : containers.values()) {
                    try {
                        container.stop();
                        logger.info("stop category listener container {}", container);
                    } catch (Exception e) {
                        logger.info("stop category listener container error ");
                    }
                }

                for (Object proxy : proxyObjectList) {
                    doProxy(proxy);
                    logger.info("reproxy rabbitmq subscribe {} ", proxy);
                }
                for (CategoryListenerContainer container : containers.values()) {
                    try {
                        container.start();
                        logger.info("start category listener container {}", container);
                    } catch (Exception e) {
                        logger.info("start category listener container error ");
                    }
                }
            }
        });
        this.channel.getConnectionFactory().addConnectionListener(new ConnectionListener() {

            private boolean disconnected = false;

            @Override
            public void onCreate(Connection connection) {
                logger.info("rabbit mq {} {} connected", channel.getProperty().getHost(), channel.getProperty().getAddresses());
                if (!disconnected) {
                    return;
                }
                disconnected = false;
                for (Object proxy : proxyObjectList) {
                    doProxy(proxy);
                    logger.info("reproxy rabbitmq subscribe {} ", proxy);
                }
            }

            @Override
            public void onClose(Connection connection) {
                logger.warn("rabbit mq {} {} disconnected", channel.getProperty().getHost(), channel.getProperty().getAddresses());
                disconnected = true;
            }
        });
    }

    public int clean(Class<?> clazz, boolean noWait) {
        String queueName = clazz.getName();
        return clean(queueName, noWait);
    }

    public int clean(String queueName, boolean noWait) {
        this.admin.purgeQueue(queueName, noWait);
        return EventErrorCode.SUCCEED;
    }

    public int proxy(Object target) {
        doProxy(target);
        proxyObjectList.add(target);
        return EventErrorCode.SUCCEED;
    }

    public int doProxy(Object target) {
        Object listener = target;
        try {
            listener = AopTargetUtils.getTarget(target);
        } catch (Exception e) {
            logger.error("proxy error.", e);
        }
        Class<?> clazz = listener.getClass();
        if (ClassUtils.isCglibProxy(listener)) {
            clazz = listener.getClass().getSuperclass();
        }
        while (clazz != null) {
            for (Method method : clazz.getMethods()) {
                Subscribe annotation = method.getAnnotation(Subscribe.class);
                if (annotation != null) {
                    try {
                        registerHandler(target, method, annotation);
                    } catch (Exception e) {
                        logger.warn("proxy error.", e);
                    }
                }
            }
            P2P p2p = clazz.getAnnotation(P2P.class);
            if (p2p != null) {
                if (p2pServerDesk == null) {
                    p2pServerDesk = new P2PServerDesk();
                    proxy(p2pServerDesk);
                }
                this.p2pServerDesk.registP2P(channel, p2p.value().getName(), target);
            }
            clazz = clazz.getSuperclass();
        }
        return EventErrorCode.SUCCEED;
    }

    private void registerHandler(Object target, Method method, Subscribe annotation) {
        if (annotation != null) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                throw new IllegalArgumentException("Method " + method + " has @Subscribe annotation, but requires " + parameterTypes.length
                        + " arguments.  Event handler methods " + "must require a single argument.");
            }
            String eventName = annotation.queueName();
            if (StringUtil.isBlank(eventName)) {
                Class<?> eventType = parameterTypes[0];
                eventName = eventType.getName();
            }
            EventHandler handler = new EventHandler(target, method);
            this.handlers.put(eventName, handler);
            Type[] types = annotation.value();
            for (Type type : types) {
                switch (type) {
                    case BROADCAST:
                        registerBroadcast(eventName, annotation);
                        break;
                    case QUEUE:
                        registerQueue(eventName, annotation);
                        break;
                    case P2P:
                        registerP2P(eventName, annotation);
                        if (p2pClientDesk == null) {
                            p2pClientDesk = new P2PClientDesk();
                        }
                        this.p2pClientDesk.registP2P(channel, eventName);
                        break;
                }
            }
        }
    }

    /**
     * 先快速加一下
     *
     * @param receiveTarget
     * @param queueName
     * @param eventClass
     * @param receiveName
     */
    public synchronized void addCustomSubscribeQueue(Object receiveTarget, String queueName, Class<?> eventClass, String receiveName) {
        Method method;
        try {
            method = ReflectionUtils.getMethod(receiveTarget.getClass(), receiveName, eventClass);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException(e);
        }
        EventHandler handler = new EventHandler(receiveTarget, method);
        this.handlers.put(eventClass.getName(), handler);
        addQueue(queueName, 0, 0);
        if (containers != null && containers.containsKey(0)) {
            CategoryListenerContainer container = containers.get(0);
            container.addSubscribeQueueName(queueName);
        }
    }

    public synchronized void removeCustomSubscribeQueue(String queueName, Class<?> eventClass) {
        this.handlers.remove(eventClass.getName());
        List<String> queueNameList = this.queueCategorys.get(0);
        if (queueNameList != null) {
            queueNameList.remove(queueName);
        }
        if (containers != null && containers.containsKey(0)) {
            CategoryListenerContainer container = containers.get(0);
            container.removeSubscribeQueueName(queueName);
        }
    }

    private synchronized void addQueue(String queueName, int p, int prefetchCount) {
        List<String> queueNameList = this.queueCategorys.get(p);
        if (queueNameList == null) {
            queueNameList = new ArrayList<String>();
            this.queueCategorys.put(p, queueNameList);
        }
        if (!queueNameList.contains(queueName)) {
            queueNameList.add(queueName);
        }
        if (!this.queueCategoryPrefetchCounts.containsKey(p) || this.queueCategoryPrefetchCounts.get(p) < prefetchCount) {
            this.queueCategoryPrefetchCounts.put(p, prefetchCount);
        }
    }

    private synchronized void addQueue(String queueName, int p, Subscribe annotation) {
        addQueue(queueName, p, annotation.prefetchCount());
    }

    public synchronized void start() {
        List<Integer> allPrioritys = new ArrayList<Integer>();
        for (Integer p : this.queueCategorys.keySet()) {
            allPrioritys.add(p);
        }
        allPrioritys.sort((o1, o2) -> (o2 - o1));
        int threadNum = channel.getProperty().getThreads();
        if (threadNum == 0) {
            threadNum = Runtime.getRuntime().availableProcessors() * 3;
        }
        this.subscribeMsgQeueu = new PriorityBlockingQueue<Runnable>(allPrioritys, threadNum);
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
        for (int p : allPrioritys) {
            List<String> queueName = this.queueCategorys.get(p);
            Integer prefetchCount = this.queueCategoryPrefetchCounts.get(p);
            if (prefetchCount == null || prefetchCount <= 0) {
                prefetchCount = threadNum * 3;
            }
            String[] queueNameArray = new String[queueName.size()];
            queueName.toArray(queueNameArray);
            if (!this.containers.containsKey(p)) {
                CategoryListenerContainer container = new CategoryListenerContainer(p, this.channel, queueNameArray, this, prefetchCount);
                container.setRabbitAdmin(this.admin);
                this.containers.put(p, container);
            }
        }
        for (CategoryListenerContainer container : this.containers.values()) {
            container.start();
        }
    }

    public void handleMessage(Object event, int priority) {
        threadPoolExecutor.execute(new RunnablePriority(priority) {

            @Override
            public void run() {
                String eventName = null;
                boolean isJsonType = false;
                String message = null;
                if (event instanceof String) {
                    String eventJson = String.valueOf(event);
                    if (eventJson.startsWith("@", 0)) {
                        String[] split = eventJson.split("##");
                        eventName = split[0].substring(1);
                        message = split[1];
                        isJsonType = true;
                    } else {
                        eventName = event.getClass().getName();
                    }
                } else {
                    eventName = event.getClass().getName();
                }
                EventHandler handler = handlers.get(eventName);
                if (handler != null) {
                    try {
                        handler.handle(eventName, isJsonType, message, event);
                    } catch (Exception e) {
                        logger.error("", e);
                    }
                }
            }
        });
    }

    private void registerP2P(String eventName, Subscribe sbscribe) {
        String queueName = eventName + "_" + ManagementFactory.getRuntimeMXBean().getName();
        Map<String, Object> args = new HashMap<String, Object>();
        if (sbscribe.queueMaxLength() > 0) {
            args.put("x-max-length", sbscribe.queueMaxLength());
        }
        if (sbscribe.messageTtl() > 0) {
            args.put("x-message-ttl", sbscribe.messageTtl());
        }
        Queue queue = new Queue(queueName, false, false, true, args);
        this.admin.declareQueue(queue);
        String tipName = queueName;
        TopicExchange exchange = new TopicExchange(tipName, false, true);
        this.admin.declareExchange(exchange);
        this.admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(eventName));
        addQueue(queueName, sbscribe.queueCategory(), sbscribe);
    }

    private void registerBroadcast(String eventName, Subscribe sbscribe) {
        String queueName = eventName + "_" + ManagementFactory.getRuntimeMXBean().getName();
        Map<String, Object> args = new HashMap<String, Object>();
        if (sbscribe.queueMaxLength() > 0) {
            args.put("x-max-length", sbscribe.queueMaxLength());
        }
        if (sbscribe.messageTtl() > 0) {
            args.put("x-message-ttl", sbscribe.messageTtl());
        }
        Queue queue = new Queue(queueName, false, false, true, args);
        this.admin.declareQueue(queue);
        FanoutExchange exchange = new FanoutExchange(eventName + "_broadcast", false, true);
        this.admin.declareExchange(exchange);
        this.admin.declareBinding(BindingBuilder.bind(queue).to(exchange));
        addQueue(queueName, sbscribe.queueCategory(), sbscribe);
    }

    private void registerQueue(String eventName, Subscribe sbscribe) {
        String queueName = eventName;
        Map<String, Object> args = new HashMap<String, Object>();
        if (sbscribe.queueMaxLength() > 0) {
            args.put("x-max-length", sbscribe.queueMaxLength());
        }
        if (sbscribe.messageTtl() > 0) {
            args.put("x-message-ttl", sbscribe.messageTtl());
        }
        Queue queue = new Queue(queueName, true, false, false, args);
        this.admin.declareQueue(queue);
        TopicExchange exchange = new TopicExchange(eventName, false, true);
        this.admin.declareExchange(exchange);
        this.admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(eventName));
        addQueue(queueName, sbscribe.queueCategory(), sbscribe);
    }
}
