package com.github.mirs.banxiaoxiao.framework.core.monitor.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mirs.banxiaoxiao.framework.core.dcc.DataListener;
import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;
import com.github.mirs.banxiaoxiao.framework.core.dcc.NodeListener;
import com.github.mirs.banxiaoxiao.framework.core.monitor.EventPublisher;
import com.github.mirs.banxiaoxiao.framework.core.monitor.app.event.AppInstaceStopEvent;
import com.github.mirs.banxiaoxiao.framework.core.monitor.app.event.AppInstanceRefreshEvent;
import com.github.mirs.banxiaoxiao.framework.core.monitor.app.event.AppInstanceStartEvent;
import com.github.mirs.banxiaoxiao.framework.core.monitor.app.event.AppStartEvent;
import com.github.mirs.banxiaoxiao.framework.core.monitor.app.event.AppStopEvent;

/**
 * @author zcy 2018年9月25日
 */
public class AppMonitor extends EventPublisher implements NodeListener {

    private Map<String, AppInstanceMonitor> appInstanceMonitors = new HashMap<String, AppInstanceMonitor>();

    private DccClient dccClient;

    public AppMonitor(DccClient dccClient) {
        this.dccClient = dccClient;
        this.dccClient.registNodeListener(ClientRegister.ROOT_APP_PATH, this);
        List<String> appList = this.dccClient.getChildren(ClientRegister.ROOT_APP_PATH);
        onRefreshChildren(appList);
    }

    @Override
    public void onDestroy(String nodeName) {
    }

    @Override
    public void onConstruct(String nodeName) {
    }

    @Override
    public synchronized void onRefreshChildren(List<String> children) {
        List<String> oldApps = new ArrayList<String>(appInstanceMonitors.keySet());
        List<String> newApps = children == null ? new ArrayList<String>() : children;
        List<String> inc = new ArrayList<String>(oldApps);
        inc.addAll(newApps);
        inc.removeAll(oldApps);
        List<String> dec = new ArrayList<String>(oldApps);
        dec.addAll(newApps);
        dec.removeAll(newApps);
        for (String app : dec) {
            AppInstanceMonitor appMonitor = this.appInstanceMonitors.get(app);
            appMonitor.destroy();
            this.appInstanceMonitors.remove(app);
        }
        for (String app : inc) {
            AppInstanceMonitor appInstanceMonitor = new AppInstanceMonitor(app, this.dccClient, this);
            if(appInstanceMonitor.appInfo != null) {
                this.appInstanceMonitors.put(app, appInstanceMonitor);
            } else {
                appInstanceMonitor.destroy();
            }
        }
    }

    static class AppInstanceMonitor implements NodeListener, DataListener<AppInfo> {

        private EventPublisher eventPublisher;

        private AppInfo appInfo;

        private DccClient dccClient;

        private List<String> instances;
        
        private String dccAppPath;

        public AppInstanceMonitor(String appName, DccClient dccClient, EventPublisher eventPublisher) {
            dccAppPath = ClientRegister.ROOT_APP_PATH.concat(DccClient.NODE_PATH_SEPARATOR).concat(appName);
            this.dccClient = dccClient;
            this.appInfo = this.dccClient.registDataListener(AppInfo.class, dccAppPath, this);
            this.eventPublisher = eventPublisher;
            if(this.appInfo != null) {
                this.dccClient.registNodeListener(dccAppPath, this);
                onRefreshChildren(this.dccClient.getChildren(dccAppPath));
            }
        }
        
        public void destroy() {
            this.dccClient.removeDataListener(dccAppPath);
            this.dccClient.removeNodeListener(dccAppPath);
        }

        @Override
        public void onDestroy(String nodeName) {
        }

        @Override
        public void onConstruct(String nodeName) {
        }

        @Override
        public synchronized void onRefreshChildren(List<String> children) {
            List<String> oldInstances = this.instances == null ? new ArrayList<String>() : this.instances;
            List<String> newInstances = children == null ? new ArrayList<String>() : children;
            boolean isAppStart = oldInstances.size() == 0 && newInstances.size() > 0;
            boolean isAppStop = oldInstances.size() > 0 && newInstances.size() == 0;
            List<String> inc = new ArrayList<String>(oldInstances);
            inc.addAll(newInstances);
            inc.removeAll(oldInstances);
            List<String> dec = new ArrayList<String>(oldInstances);
            dec.addAll(newInstances);
            dec.removeAll(newInstances);
            if (isAppStart) {
                AppStartEvent event = new AppStartEvent();
                event.setAppInfo(appInfo);
                event.setAppName(this.appInfo.getAppName());
                this.eventPublisher.notice(event);
            }
            if (isAppStop) {
                AppStopEvent event = new AppStopEvent();
                event.setAppInfo(appInfo);
                event.setAppName(this.appInfo.getAppName());
                this.eventPublisher.notice(event);
            }
            for (String instanceName : inc) {
                AppInstanceStartEvent event = new AppInstanceStartEvent();
                event.setAppInfo(appInfo);
                event.setAddress(instanceName);
                this.eventPublisher.notice(event);
            }
            for (String instanceName : dec) {
                AppInstaceStopEvent event = new AppInstaceStopEvent();
                event.setAppInfo(appInfo);
                event.setAddress(instanceName);
                this.eventPublisher.notice(event);
            }
            AppInstanceRefreshEvent event = new AppInstanceRefreshEvent();
            event.setAppInfo(appInfo);
            event.setInstances(children);
            this.eventPublisher.notice(event);
            this.instances = children;
        }

        @Override
        public void onUpdate(String path, AppInfo data) {
            this.appInfo = data;
        }
    }
}
