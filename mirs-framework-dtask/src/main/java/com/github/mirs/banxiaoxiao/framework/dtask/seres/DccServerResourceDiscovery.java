package com.github.mirs.banxiaoxiao.framework.dtask.seres;

import com.github.mirs.banxiaoxiao.framework.core.dcc.SingleDccClientHelper;
import com.github.mirs.banxiaoxiao.framework.core.event.EventListener;
import com.github.mirs.banxiaoxiao.framework.core.monitor.app.ClientQuery;
import com.github.mirs.banxiaoxiao.framework.core.monitor.app.InstanceInfo;
import com.github.mirs.banxiaoxiao.framework.core.monitor.app.event.AppInstanceRefreshEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zcy 2019年5月31日
 */
public class DccServerResourceDiscovery implements ServerResourceDiscovery, EventListener<AppInstanceRefreshEvent> {

    protected Map<String, List<ServerResource>> serverListMap = new HashMap<String, List<ServerResource>>();

    public List<String> appnames;

    private static final long TIME_SPAN = 2 * 60 * 1000L;

    private long lastClientQueryTime;

    public DccServerResourceDiscovery() {
        super();
    }

    public DccServerResourceDiscovery(List<String> appnames) {
        super();
        this.appnames = appnames;
    }

    public Map<String, List<ServerResource>> getTargetServerResourceMap() {
        Map<String, List<ServerResource>> serverListMap = new HashMap<String, List<ServerResource>>();
        ClientQuery clientQuery = new ClientQuery(SingleDccClientHelper.get());
        for (String appName : this.appnames) {
            List<ServerResource> appServerList = new ArrayList<ServerResource>();
            List<InstanceInfo> instanceInfos = clientQuery.getInstanceInfo(appName);
            for (InstanceInfo instanceInfo : instanceInfos) {
                String host = instanceInfo.getHost();
                ServerResource serverRes = new ServerResource(host, host);
                appServerList.add(serverRes);
            }
            serverListMap.put(appName, appServerList);
        }
        return serverListMap;
    }

    @Override
    public synchronized List<ServerResource> discovery() {
        long currentTime = System.currentTimeMillis();
        if (serverListMap == null || (currentTime - lastClientQueryTime) > TIME_SPAN) {
            serverListMap = getTargetServerResourceMap();
            lastClientQueryTime = currentTime;
        }
        List<ServerResource> serverList = toList(serverListMap);
        return serverList;
    }

    protected List<ServerResource> toList(Map<String, List<ServerResource>> serverListMap) {
        List<ServerResource> serverList = new ArrayList<ServerResource>();
        if (serverListMap != null) {
            serverListMap.forEach((k, v) -> {
                v.forEach((s) -> {
                    if (!serverList.contains(s)) {
                        serverList.add(s);
                    }
                });
            });
        }
        return serverList;
    }

    @Override
    public synchronized void onAppEvent(AppInstanceRefreshEvent event) {
        if (this.appnames != null && this.appnames.contains(event.getAppInfo().getAppName())) {
            List<ServerResource> appServerList = new ArrayList<ServerResource>();
            for (String host : event.getInstances()) {
                ServerResource serverRes = new ServerResource(host, host);
                appServerList.add(serverRes);
            }
            serverListMap.put(event.getAppInfo().getAppName(), appServerList);
        }
    }

    @Override
    public void setAppnames(List<String> appnames) {
        this.appnames = appnames;
    }
}
