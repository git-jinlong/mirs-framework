package com.github.mirs.banxiaoxiao.framework.core.monitor.app.event;

import com.github.mirs.banxiaoxiao.framework.core.monitor.BaseEvent;
import com.github.mirs.banxiaoxiao.framework.core.monitor.app.AppInfo;



/**
 * @author zcy 2018年9月21日
 */
public abstract class AppEvent extends BaseEvent {

    /** */
    private static final long serialVersionUID = -7129133995443165368L;
    
    private AppInfo appInfo;

    
    public AppInfo getAppInfo() {
        return appInfo;
    }

    
    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }
    
    protected String describe() {
        return appInfo == null ? "" : appInfo.toString();
    }
    
}
