package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p;

import com.github.mirs.banxiaoxiao.framework.common.util.UUID;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.EventErrorCode;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.RmqChannel;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.lang.management.ManagementFactory;
import java.util.Objects;

/**
 * mq客户端监听，当发现客户端变化时
 *
 * @author zcy 2018年10月12日
 * @see com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PMsgPublisher
 */
@Deprecated
public class P2PClient {

    private String clientId;

    private String clientAddress;

    /**
     * 最后一次心跳时间
     */
    private long heattime;

    private RmqChannel channel;

    private RabbitTemplate template;

    private String eventName;

    private String heartbeatKey;

    private String eventKey;

    private boolean expiry = false;

    public P2PClient(RmqChannel connection, String eventName) {
        this(connection, eventName, ManagementFactory.getRuntimeMXBean().getName());
    }

    public P2PClient(RmqChannel connection, String eventName, String clientAddress) {
        this.clientId = UUID.random19();
        this.clientAddress = clientAddress;
        this.channel = connection;
        if (channel != null) {
            this.template = new RabbitTemplate(this.channel.getConnectionFactory());
        }
        this.eventName = eventName;
        // this.heartbeatKey = "RMQ_P2P_HEARTBEAT_" + this.eventName;
        this.heartbeatKey = Heartbeat.class.getName() + "_broadcast";
        this.eventKey = eventName + "_" + clientAddress;
    }

    protected void sendHeart() {
        this.heattime = System.currentTimeMillis();
        Heartbeat heartbeat = new Heartbeat(heattime, clientId, clientAddress, eventName);
        template.convertAndSend(this.heartbeatKey, Heartbeat.class.getName(), heartbeat, new MessagePostProcessor() {

            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration(3000 + "");
                return message;
            }
        });
    }

    public int publish(Object event) {
        template.convertAndSend(eventKey, this.eventName, event);
        return EventErrorCode.SUCCEED;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
        this.eventKey = eventName + "_" + clientAddress;
    }

    public String getEventKey() {
        return eventKey;
    }

    public long getHeattime() {
        return heattime;
    }

    public void refreshHeattime() {
        this.heattime = System.currentTimeMillis();
    }

    public boolean isExpiry(long time) {
        return (System.currentTimeMillis() - this.heattime) > time;
    }

    public boolean isexpiry() {
        return this.expiry;
    }

    public void expiry() {
        this.expiry = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        P2PClient p2PClient = (P2PClient) o;
        return Objects.equals(eventKey, p2PClient.eventKey);
    }

    @Override
    public int hashCode() {

        return Objects.hash(eventKey);
    }
}
