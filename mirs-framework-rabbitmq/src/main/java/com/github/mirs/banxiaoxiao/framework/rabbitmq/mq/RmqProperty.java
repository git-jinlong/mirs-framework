/**
 * 
 */
package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq;

import java.io.Serializable;

/**
 * @author erxiao 2017年2月8日
 */
public class RmqProperty implements Serializable {

    /** */
    private static final long serialVersionUID = -8251209438569880182L;

    private String host;

    private int port;

    private String userName;

    private String password;

    private int threads = 0;

    private int prefetchCount;

    private int connectionTimeout;

    private int handshakeTimeout = 20000;

    private String addresses;

    private ChannelProperty channel;

    private Boolean possibleAuthenticationFailure;

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ChannelProperty getChannel() {
        return channel;
    }

    public void setChannel(ChannelProperty channel) {
        this.channel = channel;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getPrefetchCount() {
        return prefetchCount;
    }

    public void setPrefetchCount(int prefetchCount) {
        this.prefetchCount = prefetchCount;
    }

    public String getAddresses() {
        return addresses;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public int getHandshakeTimeout() {
        return handshakeTimeout;
    }

    public void setHandshakeTimeout(int handshakeTimeout) {
        this.handshakeTimeout = handshakeTimeout;
    }

    public Boolean getPossibleAuthenticationFailure() {
        return possibleAuthenticationFailure;
    }

    public void setPossibleAuthenticationFailure(Boolean possibleAuthenticationFailure) {
        this.possibleAuthenticationFailure = possibleAuthenticationFailure;
    }
}
