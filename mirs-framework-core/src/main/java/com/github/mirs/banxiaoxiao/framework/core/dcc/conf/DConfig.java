package com.github.mirs.banxiaoxiao.framework.core.dcc.conf;

import java.util.List;

/**
 * 分布式配置管理器
 * 
 * @author zcy 2019年3月12日
 * @param <v>
 */
public interface DConfig<v> {

    /**
     * 获取所有的配置项(key值)
     * 
     * @return
     */
    public List<String> all();
    
    /**
     * @param key
     * @return
     */
    public v find(String key);

    /**
     * @param key
     * @return
     */
    public boolean exist(String key);

    /**
     * 删除所有的配置项
     */
    public void delete();
    
    /**
     * 删除指定的配置项
     * @param key
     * @return
     */
    public boolean delete(String key);

    /**
     * @param key
     * @param conf
     */
    public void put(String key, v conf);
    
    /**
     * @param listener
     */
    public void addDConfigListener(DConfigListener<v> listener);
}
