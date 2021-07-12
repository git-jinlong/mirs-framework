package com.github.mirs.banxiaoxiao.framework.core.monitor.app.event;


/**
 * @author zcy 2018年9月21日
 */
public class AppStopEvent extends AppEvent {

    /** */
    private static final long serialVersionUID = 6993135274061250406L;

    private String appName;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
