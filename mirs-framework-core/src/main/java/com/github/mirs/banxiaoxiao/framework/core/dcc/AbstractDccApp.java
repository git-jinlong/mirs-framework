package com.github.mirs.banxiaoxiao.framework.core.dcc;

/**
 * 基于Dcc的一些应用扩展的抽象基类
 * 
 * @author zcy 2019年3月13日
 */
public abstract class AbstractDccApp extends AbstractDcc {

    transient private String root = "/bee";

    transient private boolean firstUse = true;

    transient private Class<?> clazz;

    public AbstractDccApp(String appKey) {
        this(appKey, null, null);
    }

    public AbstractDccApp(String appKey, Class<?> clazz) {
        this(appKey, null, clazz);
    }

    public AbstractDccApp(String appKey, DccClient dccClient, Class<?> clazz) {
        super(dccClient);
        setAppKey(appKey);
        if (this.clazz == null && clazz != null) {
            this.clazz = clazz;
        }
        if (this.clazz == null) {
            this.clazz = String.class;
        }
    }

    public void setAppKey(String appKey) {
        if (appKey.endsWith(DccClient.NODE_PATH_SEPARATOR)) {
            int index = appKey.lastIndexOf("/");
            if (index > 0) {
                appKey = appKey.substring(0, index);
            }
        }
        if (appKey.startsWith(DccClient.NODE_PATH_SEPARATOR)) {
            this.root = appKey;
        } else {
            this.root = this.root + DccClient.NODE_PATH_SEPARATOR + appKey;
        }
    }

    public void setClazz(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("target class not be null");
        }
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @SuppressWarnings("unchecked")
    protected <T> T readData(String path) {
        return (T) getDccClient().readData(path, clazz);
    }

    @SuppressWarnings("unchecked")
    protected <T> VersionData<T> readVersionData(String path) {
        return (VersionData<T>) getDccClient().readVersionData(path, clazz);
    }

    protected void writeData(String path, Object data) {
        getDccClient().writeData(path, data);
    }

    protected int writeData(String path, Object data, int version) {
        return getDccClient().writeData(path, data, version);
    }

    @Override
    public DccClient getDccClient() {
        DccClient dccClient = super.getDccClient();
        synchronized (this) {
            if (firstUse) {
                if(!dccClient.isExist(getRoot())) {
                    dccClient.writeData(getRoot(), "");
                }
                firstUse = false;
            }
        }
        return dccClient;
    }

    /**
     * 生成孩子接到路径
     * 
     * @param key
     * @return
     */
    protected String genChildPath(String key) {
        return this.root + DccClient.NODE_PATH_SEPARATOR + key;
    }

    /**
     * <pre>
     * 提取叶子节点名称，如：
     * /app0          = app0
     * /bee/app1      = app1
     * /bee/app2/     = app2
     * /bee/app3/app4 = app4
     * 
     * <pre>
     * @param path
     * @return
     */
    protected String pickLeafPath(String path) {
        String key = path;
        if (key.endsWith(DccClient.NODE_PATH_SEPARATOR)) {
            int i = key.lastIndexOf(DccClient.NODE_PATH_SEPARATOR);
            if (i > 0) {
                key = key.substring(0, i);
            }
        }
        int index = path.lastIndexOf(DccClient.NODE_PATH_SEPARATOR);
        if (index > 0) {
            key = path.substring(index + 1);
        }
        return key;
    }

    public String getRoot() {
        return this.root;
    }
}
