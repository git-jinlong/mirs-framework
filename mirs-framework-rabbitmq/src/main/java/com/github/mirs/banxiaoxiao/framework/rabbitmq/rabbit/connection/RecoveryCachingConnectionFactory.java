package com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.connection;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.RecoveryListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zcy 2019年6月27日
 */
public class RecoveryCachingConnectionFactory extends CachingConnectionFactory implements RecoveryListener,
        org.springframework.amqp.rabbit.connection.ConnectionListener {

    private List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();

    public RecoveryCachingConnectionFactory() {
        super();
        registeListener();
    }

    public RecoveryCachingConnectionFactory(com.rabbitmq.client.ConnectionFactory rabbitConnectionFactory) {
        super(rabbitConnectionFactory);
        registeListener();
    }

    private void registeListener() {
        this.setRecoveryListener(this);
        this.addConnectionListener(this);
    }

    public List<ConnectionListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<ConnectionListener> listeners) {
        this.listeners = listeners;
    }

    public void addListener(ConnectionListener listener) {
        if(this.listeners == null) {
            this.listeners = new ArrayList<>();
        }
        this.listeners.add(listener);
    }

    public void removeListener(ConnectionListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void onCreate(Connection connection) {
        if (this.listeners != null) {
            for (ConnectionListener listener : this.listeners) {
                try {
                    listener.onConnected(connection);
                } catch (Throwable e) {
                    TComLogs.error("", e);
                }
            }
        }
    }

    @Override
    public void onClose(Connection connection) {
        if (this.listeners != null) {
            for (ConnectionListener listener : this.listeners) {
                try {
                    listener.onDisconnect(connection);
                } catch (Throwable e) {
                    TComLogs.error("", e);
                }
            }
        }
    }

    @Override
    public void handleRecovery(Recoverable recoverable) {
        if (this.listeners != null) {
            for (ConnectionListener listener : this.listeners) {
                try {
                    listener.onReconnected();
                } catch (Throwable e) {
                    TComLogs.error("", e);
                }
            }
        }
    }

    @Override
    public void handleRecoveryStarted(Recoverable recoverable) {
        if (this.listeners != null) {
            for (ConnectionListener listener : this.listeners) {
                try {
                    listener.onReconnectStart();
                } catch (Throwable e) {
                    TComLogs.error("", e);
                }
            }
        }
    }
}
