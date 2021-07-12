package com.github.mirs.banxiaoxiao.framework.core.cross.config;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Auther: lxj
 * @Date: 2020/4/7 17:08
 * @Description:
 */
public class CrossFilterMapCacheHelper {

    private final static Map<String,Object> invocationCache = Maps.newConcurrentMap();

    public static Object get(String key){
        return invocationCache.get(key);
    }

    public static void put(String key,Object value){
        invocationCache.put(key,value);
    }

    public static void clear(){
        invocationCache.clear();
    }
}
