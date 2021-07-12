/**
 * 
 */
package com.github.mirs.banxiaoxiao.framework.core.drpc.group;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;

/**
 * @author erxiao 2017年7月17日
 */
public class GroupDServiceReferencers {

    static ApplicationContext applicationContext;

    static Map<String, GroupDServiceReferencer<?>> references = new HashMap<String, GroupDServiceReferencer<?>>();

    @SuppressWarnings("unchecked")
    public synchronized static <T> T referencer(Class<T> serviceClass, String groupName) {
        String key = "@" + serviceClass + ",g=" + groupName;
        GroupDServiceReferencer<T> referencer = (GroupDServiceReferencer<T>) references.get(key);
        if (referencer != null) {
            return referencer.get();
        } else {
            referencer = new GroupDServiceReferencer<T>(serviceClass, groupName);
            referencer.setApplicationContext(applicationContext);
            T proxy = referencer.get();
            references.put(key, referencer);
            return proxy;
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized static <T> T clear(Class<T> serviceClass, String groupName) {
        String key = "@" + serviceClass + ",g=" + groupName;
        GroupDServiceReferencer<T> referencer = (GroupDServiceReferencer<T>) references.remove(key).get();
        if (referencer != null) {
            referencer.destroy();
            return referencer.get();
        } else {
            return null;
        }
    }

    public synchronized static void clear() {
        for (GroupDServiceReferencer<?> reference : references.values()) {
            try {
                reference.destroy();
            } catch (Exception e) {
                TComLogs.error("", e);
            }
        }
        references.clear();
    }

    @Configuration
    static class CacheDubboGroupAutoConfig {

        @Autowired
        public void setApplicationContext(ApplicationContext applicationContext) {
            GroupDServiceReferencers.applicationContext = applicationContext;
        }
    }
}
