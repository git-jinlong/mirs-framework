package com.github.mirs.banxiaoxiao.framework.core.drpc.router;

import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.RegistryFactory;
import org.springframework.stereotype.Component;

/**
 * @类名: DynamicRouterManager
 * @描述: DynamicRouterManager
 * @作者: liudf
 * @日期: 2019/9/26 10:27
 */
@Component
public class DynamicRouterManager {

    /**
     * 注册路由规则
     * @param interfaceKey
     */
    public void registerRouter(String interfaceKey) {
        String zkhost = BeeClientConfiguration.getLocalProperies().getProperty("bee.dcc.zkHost");
        if (zkhost.startsWith("${")) {
            String key = zkhost.substring(zkhost.indexOf("${") + 2, zkhost.indexOf("}"));
            String realVal = BeeClientConfiguration.getLocalProperies().getProperty(key);
            if (!StringUtil.isBlank(realVal)) {
                zkhost = realVal;
            }
        }
        URL url = URL.valueOf("zookeeper://"+zkhost);
        RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();
        Registry registry = registryFactory.getRegistry(url);
        registry.register(URL.valueOf("dynamic://"+zkhost+"/"+interfaceKey+"?category=routers&router=dynamic&dynamic=true"));
    }

    /**
     * 取消注册路由规则（切换必须要先取消，否则无法重新注入规则）
     * @param interfaceKey
     */
    public void unregisterRouter(String interfaceKey) {
        String zkhost = BeeClientConfiguration.getLocalProperies().getProperty("bee.dcc.zkHost");
        if (zkhost.startsWith("${")) {
            String key = zkhost.substring(zkhost.indexOf("${") + 2, zkhost.indexOf("}"));
            String realVal = BeeClientConfiguration.getLocalProperies().getProperty(key);
            if (!StringUtil.isBlank(realVal)) {
                zkhost = realVal;
            }
        }
        URL url = URL.valueOf("zookeeper://"+zkhost);
        RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();
        Registry registry = registryFactory.getRegistry(url);
        registry.unregister(URL.valueOf("dynamic://"+zkhost+"/"+interfaceKey+"?category=routers&router=dynamic&dynamic=true"));
    }
}
