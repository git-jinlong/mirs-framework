package com.github.mirs.banxiaoxiao.framework.core.dcc.client;

import java.util.List;

import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.dcc.AbstractDccApp;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

/**
 * 同一jvm内只能有一个clientId，全局唯一
 * 
 * @author zcy 2019年3月18日
 */
public class ClientId extends AbstractDccApp {

    private String clientId;

    public ClientId() {
        super("client");
    }

    private void applyId() {
        if (this.clientId == null) {
            String appName = BeeClientConfiguration.getLocalProperies().getAppName();
            String path = genAppClientPath(appName);
            getDccClient().writeData(path, appName);
            this.clientId = getDccClient().createTmpSeq(getRoot() + "/c");
            int index = this.clientId.lastIndexOf("/");
            if(index > 0) {
                this.clientId = this.clientId.substring(index + 1);
            }
        }
    }

    private String getClientId() {
        return this.clientId;
    }

    private List<String> getAllClientId() {
        String appName = BeeClientConfiguration.getLocalProperies().getAppName();
        String path = genAppClientPath(appName);
        return getDccClient().getChildren(path);
    }

    private List<String> getAllClientId(String appName) {
        String path = genAppClientPath(appName);
        return getDccClient().getChildren(path);
    }

    private String genAppClientPath(String appName) {
        return getRoot() + "/" + appName;
    }

    public static String get() {
        ClientId instance = SingleContainer.INSTANCE;
        synchronized (instance) {
            if (instance.clientId == null) {
                instance.applyId();
            }
        }
        String id = instance.getClientId();
        if (StringUtil.isBlank(id)) {
            throw new IllegalStateException("No ID applied");
        }
        return id;
    }

    public static List<String> getClientIds() {
        return SingleContainer.INSTANCE.getAllClientId();
    }

    public static List<String> getClientIds(String appName) {
        return SingleContainer.INSTANCE.getAllClientId(appName);
    }

    static class SingleContainer {

        private static ClientId INSTANCE = new ClientId();
    }
}
