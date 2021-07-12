package com.github.mirs.banxiaoxiao.framework.core.monitor.app;

import java.io.Serializable;

/**
 * @author zcy 2018年9月21日
 */
public class InstanceInfo implements Serializable {

    /** */
    private static final long serialVersionUID = -6287824429475888757L;
    private String clientId;
    
    private String host;

    private String startTime;

    private String lastUpdateTime;
    
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
