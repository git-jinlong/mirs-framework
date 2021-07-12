package com.github.mirs.banxiaoxiao.framework.core.dcc.conf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 具有本地缓存能力的配置管理器，
 * 
 * @author zcy 2019年3月12日
 */
public class CacheDConfig<v> extends BaseDConfig<v> {

    private int time;

    private int outime;

    /** 多长时间检查下已过期（该删除缓存）的缓存节点 */
    private int checkOutime;

    /** 最后一次检查已过期缓存节点的时间 */
    private long lastCheckOutime;

    private Map<String, ConfigCacheNode<v>> configMap = new HashMap<String, ConfigCacheNode<v>>();

    public CacheDConfig(String namespace) {
        this(namespace, 30000);
    }

    /**
     * @param root
     * @param time
     *            获取新配置内容的时间间隔，单位毫秒
     */
    public CacheDConfig(String namespace, int time) {
        this(namespace, time, 3600000);
    }

    /**
     * @param root
     * @param time
     *            获取新配置内容的时间间隔，单位毫秒
     * @param outime
     *            配置一段时间内未被使用则移除，单位毫秒
     */
    public CacheDConfig(String namespace, int time, int outime) {
        super(namespace);
        this.time = time;
        this.outime = outime;
        this.checkOutime = 30000;
        this.lastCheckOutime = System.currentTimeMillis();
    }

    public CacheDConfig(String namespace, Class<v> clazz) {
        this(namespace);
        setClazz(clazz);
    }

    public CacheDConfig(String namespace, Class<v> clazz, int time) {
        this(namespace, time);
        setClazz(clazz);
    }

    public CacheDConfig(String namespace, Class<v> clazz, int time, int outime) {
        this(namespace, time, outime);
        setClazz(clazz);
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setOutime(int outime) {
        this.outime = outime;
    }

    public void setCheckOutime(int checkOutime) {
        this.checkOutime = checkOutime;
    }

    /**
     * 获取所有的配置项，该方法不受缓存时间限制，每次请求都是获取实时数据
     * 
     */
    @Override
    public List<String> all() {
        return super.all();
    }

    @Override
    public v find(String key) {
        return findCacheNode(key).getData();
    }
    
    public v findReal(String key) {
        return super.find(key);
    }

    @Override
    public boolean exist(String key) {
        return find(key) != null;
    }

    @Override
    public boolean delete(String key) {
        deleteCache(key);
        return super.delete(key);
    }

    @Override
    public void put(String key, v conf) {
        super.put(key, conf);
        ConfigCacheNode<v> node = findCacheNode(key);
        node.setData(conf);
    }

    public void cleanCache() {
        configMap.clear();
    }
    
    protected ConfigCacheNode<v> deleteCache(String key) {
        return configMap.remove(key);
    }

    protected ConfigCacheNode<v> findCacheNode(String key) {
        ConfigCacheNode<v> node = configMap.get(key);
        boolean getNew = false;
        if (node == null) {
            synchronized (this) {
                node = configMap.get(key);
                if (node == null) {
                    getNew = true;
                    node = new ConfigCacheNode<v>();
                    configMap.put(key, node);
                }
            }
        } else if (node.isExpiryDataTime(time)) {
            getNew = true;
        }
        if (getNew) {
            synchronized (this) {
                if (node.isExpiryDataTime(time)) {
                    v data = findReal(key);
                    node.setData(data);
                }
            }
        }
        node.refreshUseTime();
        // 清除那些长时间未使用的cache
        tryCleanTimeoutNode();
        return node;
    }

    protected void tryCleanTimeoutNode() {
        long now = System.currentTimeMillis();
        if ((now - this.lastCheckOutime) > this.checkOutime) {
            synchronized (this) {
                Iterator<Map.Entry<String, ConfigCacheNode<v>>> it = this.configMap.entrySet().iterator();
                while (it.hasNext()) {
                    ConfigCacheNode<v> node = it.next().getValue();
                    if (node.isExpiryLastUseTime(now, this.outime)) {
                        // 失效后移除一下监听器
                        removeDataListener(node.getKey());
                        it.remove();
                    }
                }
                this.lastCheckOutime = System.currentTimeMillis();
            }
        }
    }

    static class ConfigCacheNode<v> {

        private String key;

        private long lastDataTime;

        private long lastUseTime;

        private v data;

        public void refreshDataTime() {
            this.lastDataTime = System.currentTimeMillis();
        }

        public void refreshUseTime() {
            this.lastUseTime = System.currentTimeMillis();
        }

        public boolean isExpiryDataTime(int millistime) {
            long now = System.currentTimeMillis();
            return (now - this.lastDataTime) > millistime;
        }

        public boolean isExpiryLastUseTime(int millistime) {
            long now = System.currentTimeMillis();
            return isExpiryLastUseTime(now, millistime);
        }

        public boolean isExpiryLastUseTime(long now, int expiry) {
            return (now - this.lastUseTime) > expiry;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public v getData() {
            return data;
        }

        public void setData(v data) {
            this.data = data;
            refreshDataTime();
        }
    }
}
