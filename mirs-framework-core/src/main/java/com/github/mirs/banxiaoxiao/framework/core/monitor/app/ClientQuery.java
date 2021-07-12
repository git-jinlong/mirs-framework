package com.github.mirs.banxiaoxiao.framework.core.monitor.app;

import java.util.ArrayList;
import java.util.List;

import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;

/**
 * @author zcy 2018年9月21日
 */
public class ClientQuery {

    private DccClient dccClient;

    public ClientQuery(DccClient dccClient) {
        this.dccClient = dccClient;
    }
    
    public AppInfo getAppInfo(String appName) {
        String appPath = ClientRegister.ROOT_APP_PATH.concat(DccClient.NODE_PATH_SEPARATOR).concat(appName);
        AppInfo appInfo = this.dccClient.readData(appPath, AppInfo.class);
        return appInfo;
    }

    /**
     * 获取app列表，曾经启动注册过但是下线了的app也会查询出来。如果要查询app是否有在线服务器，请查询 {@link #getInstanceInfo(String)}
     * 
     * @return
     */
    public List<AppInfo> getAppInfo() {
        List<AppInfo> appInfos = new ArrayList<AppInfo>();
        List<String> children = this.dccClient.getChildren(ClientRegister.ROOT_APP_PATH);
        if (children != null) {
            for (String child : children) {
                String appPath = ClientRegister.ROOT_APP_PATH.concat(DccClient.NODE_PATH_SEPARATOR).concat(child);
                AppInfo appInfo = this.dccClient.readData(appPath, AppInfo.class);
                if (appInfo != null) {
                    appInfos.add(appInfo);
                }
            }
        }
        return appInfos;
    }

    public List<InstanceInfo> getInstanceInfo(String appName) {
        String appPath = ClientRegister.ROOT_APP_PATH.concat(DccClient.NODE_PATH_SEPARATOR).concat(appName);
        List<InstanceInfo> instanceInfos = new ArrayList<InstanceInfo>();
        List<String> children = this.dccClient.getChildren(appPath);
        if (children != null) {
            for (String child : children) {
                String instancePath = appPath.concat(DccClient.NODE_PATH_SEPARATOR).concat(child);
                InstanceInfo instanceInfo = this.dccClient.readData(instancePath, InstanceInfo.class);
                if (instanceInfo != null) {
                    instanceInfos.add(instanceInfo);
                }
            }
        }
        return instanceInfos;
    }
}
