package com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.config;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.RmqProperty;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PMsgPublisherFactory;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PRabbitMsgPublisherFactory;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.RabbitRmqAdmin;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.connection.RecoveryCachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties("bee.rmq")
public class RabbitRmqAutoConfiguration extends RmqProperty {

    /**
     *
     */
    private static final long serialVersionUID = 3191721153520914375L;

    @Bean
    public CachingConnectionFactory rabbitConnectionFactory() throws Exception {
        RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
        factory.setAutomaticRecoveryEnabled(isSpringAutomaticRecoveryEnabled());
        if (getHost() != null) {
            factory.setHost(getHost());
        }
        if (getPort() > 0) {
            factory.setPort(getPort());
        }
        if (getUserName() != null) {
            factory.setUsername(getUserName());
        }
        if (getPassword() != null) {
            factory.setPassword(getPassword());
        }
        if (getConnectionTimeout() > 0) {
            factory.setConnectionTimeout(getConnectionTimeout());
        }
        factory.afterPropertiesSet();
        RecoveryCachingConnectionFactory connectionFactory = new RecoveryCachingConnectionFactory(factory.getObject());
        if (!StringUtil.isBlank(getHost())) {
            connectionFactory.setHost(getHost());
        }
        if (!StringUtil.isBlank(getAddresses())) {
            connectionFactory.setAddresses(getAddresses());
        }
        connectionFactory.setUsername(getUserName());
        connectionFactory.setPassword(getPassword());
        connectionFactory.setChannelCacheSize(getChannelSize());
        if (getConnectionTimeout() > 0) {
            connectionFactory.setConnectionTimeout(getConnectionTimeout());
        }
        if (getHandshakeTimeout() > 0) {
            connectionFactory.getRabbitConnectionFactory().setHandshakeTimeout(getHandshakeTimeout());
        }
        if (getChannelCheckoutTimeout() > 0) {
            connectionFactory.setChannelCheckoutTimeout(getChannelCheckoutTimeout());
        }
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        if (getReceiveTimeout() > 0) {
            rabbitTemplate.setReceiveTimeout(getReceiveTimeout());
        }
        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitRmqAdmin rabbitRmqAdmin(RmqProperty property, RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin) {
        return new RabbitRmqAdmin(property, rabbitTemplate, rabbitAdmin);
    }

    @Bean
    public P2PMsgPublisherFactory msgPublisherFactory(RabbitRmqAdmin rabbitRmqAdmin) {
        return new P2PRabbitMsgPublisherFactory(rabbitRmqAdmin);
    }
}
