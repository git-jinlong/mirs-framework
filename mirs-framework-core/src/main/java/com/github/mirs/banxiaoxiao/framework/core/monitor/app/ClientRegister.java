package com.github.mirs.banxiaoxiao.framework.core.monitor.app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;
import com.github.mirs.banxiaoxiao.framework.core.dcc.NodeListener;
import com.github.mirs.banxiaoxiao.framework.core.dcc.client.ClientId;

/**
 * @author zcy 2018年9月21日
 */
public class ClientRegister {

    private Logger logger = LoggerFactory.getLogger(ClientRegister.class);

    public static String ROOT_PATH = "/bee/monitor";

    public static String ROOT_APP_PATH = "/bee/monitor/app";

    private DccClient dccClient;

    public ClientRegister(DccClient dccClient) {
        dccClient.writeData(ROOT_APP_PATH, "");
        this.dccClient = dccClient;
    }

    public void registeApp(String appName, String address, int drpcPort, int httpPort) {
        String appPath = ROOT_APP_PATH.concat(DccClient.NODE_PATH_SEPARATOR).concat(appName);
        String instancePath = ROOT_APP_PATH.concat(DccClient.NODE_PATH_SEPARATOR).concat(appName).concat(DccClient.NODE_PATH_SEPARATOR)
                .concat(address);
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        AppInfo appInfo = new AppInfo();
        appInfo = new AppInfo();
        appInfo.setHttpPort(httpPort);
        appInfo.setDrpcPort(drpcPort);
        appInfo.setStartTime(time);
        appInfo.setAppName(appName);
        appInfo.setStartTime(time);
        appInfo.setLastUpdateTime(time);
        InstanceInfo instanceInfo = new InstanceInfo();
        instanceInfo.setClientId(ClientId.get());
        instanceInfo.setStartTime(time);
        instanceInfo.setHost(address);
        instanceInfo.setStartTime(time);
        instanceInfo.setLastUpdateTime(time);
        this.dccClient.writeData(appPath, appInfo);
        this.dccClient.registNodeListener(instancePath, new NodeListener() {

            @Override
            public void onRefreshChildren(List<String> children) {
            }

            @Override
            public void onDestroy(String nodeName) {
                //  似乎出现过死循环，dccClient.writeTempData写入节点信息后，立马又会收到onDestroy事件，然后又去registeApp
                logger.error("app {} drop line", appName);
                registeApp(appName, address, drpcPort, httpPort);
            }

            @Override
            public void onConstruct(String nodeName) {
                logger.info("app {} on line", appName);
            }
        });
        this.dccClient.writeTempData(instancePath, instanceInfo);
        logger.info("register app " + appName);
    }
}
