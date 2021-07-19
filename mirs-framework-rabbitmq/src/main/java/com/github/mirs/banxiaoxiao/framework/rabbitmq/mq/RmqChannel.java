package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

/**
 * @author zcy 2018年9月11日
 */
public class RmqChannel {

    private CachingConnectionFactory connectionFactory;
    private RmqProperty property;

    public RmqChannel(RmqProperty p) {
        this.property = p;
    }

    public void connect() {
        com.rabbitmq.client.ConnectionFactory rmqcFactory = new com.rabbitmq.client.ConnectionFactory();
        rmqcFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory = new CachingConnectionFactory(rmqcFactory);
        if (!StringUtil.isBlank(this.property.getHost())) {
            connectionFactory.setHost(this.property.getHost());
        }
        if (!StringUtil.isBlank(this.property.getAddresses())) {
            connectionFactory.setAddresses(this.property.getAddresses());
        }
        connectionFactory.setUsername(this.property.getUserName());
        connectionFactory.setPassword(this.property.getPassword());
        if (this.property.getConnectionTimeout() > 0) {
            connectionFactory.setConnectionTimeout(this.property.getConnectionTimeout());
        }
        if (this.property.getHandshakeTimeout() > 0) {
            connectionFactory.getRabbitConnectionFactory().setHandshakeTimeout(this.property.getHandshakeTimeout());
        }
    }

    public void disconnect() {
        connectionFactory.destroy();
    }

    public CachingConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }


    public RmqProperty getProperty() {
        return property;
    }

}
