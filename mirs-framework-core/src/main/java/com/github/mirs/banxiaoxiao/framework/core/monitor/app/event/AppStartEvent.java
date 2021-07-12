package com.github.mirs.banxiaoxiao.framework.core.monitor.app.event;


/**
 * @author zcy 2018年9月21日
 */
public class AppStartEvent extends AppEvent {

    /**  */
    private static final long serialVersionUID = -4090185865114277428L;

    private String appName;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
