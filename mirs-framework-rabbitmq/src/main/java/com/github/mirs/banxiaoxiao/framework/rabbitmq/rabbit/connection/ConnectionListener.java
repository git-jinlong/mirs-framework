package com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.connection;

import org.springframework.amqp.rabbit.connection.Connection;

/**
 * @author zcy 2019年6月27日
 */
public interface ConnectionListener {

    public void onConnected(Connection connection);

    public void onDisconnect(Connection connection);

    public void onReconnected();
    
    public void onReconnectStart();
}
