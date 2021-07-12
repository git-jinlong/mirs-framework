package com.github.mirs.banxiaoxiao.framework.core.dcc.conf;

import java.util.List;

/**
 * 配置监听器，配置发送变化后会实时回调给监听者。请确保监听器业务逻辑是轻量级的，如果有比较耗时的业务操作建议异步处理
 * 
 * @author zcy 2019年3月12日
 * @param <v>
 */
public interface DConfigListener<v> {

    /**
     * @param key
     * @param config
     */
    public void onConfigCreate(String key, v config);

    /**
     * @param key
     */
    public void onConfigDelete(String key);

    /**
     * @param key
     * @param config
     */
    public void onConfigUpdate(String key, v config);

    /**
     * 当配置项发生增加、删除等操作时回调本方法，onChildrenChanage回调会同时触发onConfigDelete或者onConfigCreate回调
     * 
     * @param configKeyList
     */
    public void onChildrenChanage(List<String> configKeyList);
}
