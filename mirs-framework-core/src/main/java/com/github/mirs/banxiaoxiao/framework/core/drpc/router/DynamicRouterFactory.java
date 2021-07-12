package com.github.mirs.banxiaoxiao.framework.core.drpc.router;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.cluster.RouterFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @类名: DynamicSetRouterFactory
 * @描述: 动态路由工厂类
 * @作者: liudf
 * @日期: 2019/9/20 15:04
 */
public class DynamicRouterFactory implements RouterFactory {
    public static final String NAME = "dynamic";

    private ConcurrentHashMap<URL,Router> routerMap = new ConcurrentHashMap<>();

    @Override
    public Router getRouter(URL url) {
        Router router = null;
        if (routerMap.get(url) != null){
            return routerMap.get(url);
        }
        synchronized (this) {
            if (routerMap.get(url) == null) {
                router = createRouter(url);
                routerMap.put(url,router);
                return router;
            }
        }
        return router;
    }

    private Router createRouter(URL url) {
        return new DynamicRouter(url);
    }
}
