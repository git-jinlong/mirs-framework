package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p;

import java.io.Serializable;

/**
 * 客户端心跳包
 * 
 * @author zcy 2018年10月12日
 */
public class Heartbeat implements Serializable {

    /** */
    private static final long serialVersionUID = 2562248475931662256L;

    private long time;

    private String clientId;

    private String clientAddress;

    private String eventName;

    public Heartbeat(long time, String clientId, String clientAddress, String eventName) {
        super();
        this.time = time;
        this.clientId = clientId;
        this.clientAddress = clientAddress;
        this.eventName = eventName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
