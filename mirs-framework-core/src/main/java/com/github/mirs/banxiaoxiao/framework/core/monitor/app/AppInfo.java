package com.github.mirs.banxiaoxiao.framework.core.monitor.app;

import java.io.Serializable;

/**
 * @author zcy 2018年9月21日
 */
public class AppInfo implements Serializable {

    /** */
    private static final long serialVersionUID = 7451885768771393102L;

    private String appName;

    private String startTime;

    private String lastUpdateTime;

    private int drpcPort;

    private int httpPort;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getDrpcPort() {
        return drpcPort;
    }

    public void setDrpcPort(int drpcPort) {
        this.drpcPort = drpcPort;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
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

    public String toString() {
        return "AppInfo [" + getAppName() + "," + getStartTime() + "]";
    }
}
