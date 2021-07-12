package com.github.mirs.banxiaoxiao.framework.core.dcc.conf;

import java.util.List;

/**
 * 基于dcc主动推送的实时性更高的本地缓存分布式配置管理器
 * 
 * @author zcy 2019年3月12日
 */
public class PushCacheDConfig<v> extends CacheDConfig<v> implements DConfigListener<v> {

    private static int defaultExpiryTime = 30000;

    private static int defaultOutime = 3600000;

    public PushCacheDConfig(String namespace) {
        this(namespace, defaultExpiryTime);
    }

    /**
     * @param root
     * @param time
     *            获取新配置内容的时间间隔，单位毫秒
     */
    public PushCacheDConfig(String namespace, int time) {
        this(namespace, time, defaultOutime);
    }

    /**
     * @param root
     * @param time
     *            获取新配置内容的时间间隔，单位毫秒
     * @param outime
     *            配置一段时间内未被使用则移除，单位毫秒
     */
    public PushCacheDConfig(String namespace, int time, int outime) {
        this(namespace, null, time, outime);
    }

    public PushCacheDConfig(String namespace, Class<v> clazz) {
        this(namespace, clazz, defaultExpiryTime);
    }

    public PushCacheDConfig(String namespace, Class<v> clazz, int time) {
        this(namespace, clazz, time, defaultOutime);
    }

    public PushCacheDConfig(String namespace, Class<v> clazz, int time, int outime) {
        super(namespace, time, outime);
        if (clazz != null) {
            setClazz(clazz);
        }
        startListen();
    }

    private void startListen() {
        startChildDataListen();
        startChildListListen();
        addDConfigListener(this);
    }

    @Override
    public void onConfigCreate(String key, v config) {
        onConfigUpdate(key, config);
    }

    @Override
    public void onConfigDelete(String key) {
        deleteCache(key);
    }

    @Override
    public void onChildrenChanage(List<String> configKeyList) {
        cleanCache();
        if (configKeyList != null) {
            for (String key : configKeyList) {
                v config = findReal(key);
                onConfigUpdate(key, config);
            }
        }
    }

    @Override
    public void onConfigUpdate(String key, v config) {
        ConfigCacheNode<v> node = findCacheNode(key);
        node.setData(config);
    }
}
