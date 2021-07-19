package com.github.mirs.banxiaoxiao.framework.rabbitmq;

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

    private int receiveTimeout = 1000;

    private int handshakeTimeout = 20000;
    
    private int channelCheckoutTimeout = 0;

    private int channelSize = 50;

    private String addresses;

    private Boolean possibleAuthenticationFailure;

    private boolean springAutomaticRecoveryEnabled = true;

    private boolean channelTransacted = true;

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
    
    public int getChannelCheckoutTimeout() {
        return channelCheckoutTimeout;
    }
    
    public void setChannelCheckoutTimeout(int channelCheckoutTimeout) {
        this.channelCheckoutTimeout = channelCheckoutTimeout;
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

    public int getReceiveTimeout() {
        return receiveTimeout;
    }

    public void setReceiveTimeout(int receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    public int getChannelSize() {
        return channelSize;
    }

    public void setChannelSize(int channelSize) {
        this.channelSize = channelSize;
    }

    public boolean isSpringAutomaticRecoveryEnabled() {
        return springAutomaticRecoveryEnabled;
    }

    public void setSpringAutomaticRecoveryEnabled(boolean springAutomaticRecoveryEnabled) {
        this.springAutomaticRecoveryEnabled = springAutomaticRecoveryEnabled;
    }

    public boolean isChannelTransacted() {
        return channelTransacted;
    }

    public void setChannelTransacted(boolean channelTransacted) {
        this.channelTransacted = channelTransacted;
    }
}
