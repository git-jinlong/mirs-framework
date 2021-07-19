package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.consult;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.core.util.DataTimeWindow;
import com.github.mirs.banxiaoxiao.framework.core.util.DataTimeWindowListener;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.*;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PClientListener;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.RabbitMsgChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsultationDesk implements DataTimeWindowListener<P2PHeartbeat>, ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private MsgPublisherFactory publisherFactory;

    @Resource
    private MsgChannelAdmin rmqAdmin;

    @Autowired(required = false)
    private List<P2PClientListener> listeners;

    private MsgPublisher broadcast;

    private AtomicBoolean isLeader = new AtomicBoolean(false);

    private DataTimeWindow<P2PHeartbeat> dataWindow;

    private static List<String> EMPTY = new ArrayList<String>();

    @PostConstruct
    public void init() {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put(RabbitMsgChannel.CHANNEL_AUTOCREATE_KEY, true);
        MsgChannel broadcastChannel = rmqAdmin.declareChannel(ConsultSession.class.getName(), MsgChannelType.BROADCAST, args);
        broadcast = publisherFactory.getMsgPublisher(MsgChannelType.BROADCAST, ConsultSession.class.getName());
        rmqAdmin.bindSubscribe(1, broadcastChannel, this, "onConsultSession");
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        /*
          - waiting to lock <0x00000007b9d135a0> (a java.util.concurrent.ConcurrentHashMap)
        at org.springframework.beans.factory.support.AbstractBeanFactory.isTypeMatch(AbstractBeanFactory.java:492)
         - locked <0x00000007b89189f0> (a java.lang.Object)
        at org.springframework.amqp.rabbit.core.RabbitTemplate.doExecute(RabbitTemplate.java:1436)
        
        - waiting to lock <0x00000007b89189f0> (a java.lang.Object)
        at org.springframework.amqp.rabbit.connection.ConnectionFactoryUtils$1.createConnection(ConnectionFactoryUtils.java:90)
        at org.springframework.amqp.rabbit.connection.ConnectionFactoryUtils.doGetTransactionalResourceHolder(ConnectionFactoryUtils.java:140)
        
        at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:230)
        - locked <0x00000007b9d135a0> (a java.util.concurrent.ConcurrentHashMap)
        at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:308)
        at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:197)
         * */
        if (dataWindow == null) {
            dataWindow = new DataTimeWindow<P2PHeartbeat>(TimeUnit.SECONDS, 10, this);
            dataWindow.setEmptyNotify(true);
        }
        ConsultationDeskHelper.set(this);
    }

    private synchronized void onHeartbeat(P2PHeartbeat heartbeat) {
        P2PHeartbeat exist = dataWindow.pick(heartbeat);
        dataWindow.push(heartbeat);
        if (exist == null) {
            notifyClientListener();
        } else {
            List<String> existMsgTypes = exist.getSupportMsgTypes();
            List<String> msgTypes = heartbeat.getSupportMsgTypes();
            if (existMsgTypes == null) {
                existMsgTypes = EMPTY;
            }
            if (msgTypes == null) {
                msgTypes = EMPTY;
            }
            if (!(existMsgTypes.containsAll(msgTypes) && msgTypes.containsAll(existMsgTypes))) {
                notifyClientListener();
            }
        }
    }

    private void notifyClientListener() {
        if (listeners != null && listeners.size() > 0) {
            List<P2PHeartbeat> clients = dataWindow.gets();
            for (P2PClientListener listener : listeners) {
                try {
                    listener.onHeartbeat(clients);
                } catch (Exception e) {
                    TComLogs.error("notify p2p client listener fail", e);
                }
            }
        }
    }

    public synchronized void onConsultSession(ConsultSession<?> session) {
        switch (session.getEventType()) {
            case HEARTBEAT:
                onHeartbeat((P2PHeartbeat) session.getBody());
                break;
            default:
                TComLogs.error("not supported event type", session.getEventType());
        }
    }

    private int count = 0;

    private int count2 = 0;

    @Override
    public void onInvalid(List<P2PHeartbeat> data) {
        if (count == 0 || count > 4) {
            // 利用时间窗失效来发送心跳
            sendHeartbeat();
        }
        count++;
        count2++;
        if (data != null && data.size() > 0) {
            notifyClientListener();
        }
    }

    private void sendHeartbeat() {
        List<String> supportMsgTypes = getSupportMsgTypes();
        P2PHeartbeat heartbeat = new P2PHeartbeat();
        heartbeat.setTime(System.currentTimeMillis());
        heartbeat.setHost(RabbitMsgChannel.getLocalHostProcessKey());
        heartbeat.setLeader(isLeader.get());
        heartbeat.setSupportMsgTypes(supportMsgTypes);
        ConsultSession<P2PHeartbeat> session = new ConsultSession<P2PHeartbeat>();
        session.setBody(heartbeat);
        session.setEventType(ConsultEventType.HEARTBEAT);
        broadcast.publish(session, 3000);
        count = 1;
    }

    private List<String> localP2PMsgTypes;

    private List<String> getSupportMsgTypes() {
        if (localP2PMsgTypes == null || count2 > 50) {
            localP2PMsgTypes = new ArrayList<>();
            List<MsgChannel> channels = rmqAdmin.getSubscribeMsgChannels();
            if (channels != null) {
                for (MsgChannel msgChannel : channels) {
                    if (msgChannel.getChannelType() == MsgChannelType.P2P) {
                        localP2PMsgTypes.add(msgChannel.getMsgType());
                    }
                }
            }
            count2 = 0;
        }
        return localP2PMsgTypes;
    }

    public MsgPublisherFactory getPublisherFactory() {
        return publisherFactory;
    }

    public void setPublisherFactory(MsgPublisherFactory publisherFactory) {
        this.publisherFactory = publisherFactory;
    }

    public MsgChannelAdmin getRmqAdmin() {
        return rmqAdmin;
    }

    public void setRmqAdmin(MsgChannelAdmin rmqAdmin) {
        this.rmqAdmin = rmqAdmin;
    }

    public List<P2PClientListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<P2PClientListener> listeners) {
        this.listeners = listeners;
    }

    public void addP2PClientListener(P2PClientListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<>();
        }
        this.listeners.add(listener);
        if (dataWindow != null) {
            listener.onHeartbeat(dataWindow.gets());
        }
    }

}
