/**
 *
 */
package com.github.mirs.banxiaoxiao.framework.core.config;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

/**
 * @author erxiao 2017年3月28日
 */
public class BeeClientConfiguration {

    private static BeeClientConfiguration INSTANCE = new BeeClientConfiguration();

    private BeeClientLocalProperties localProperies;

    private BeeClientConfiguration() {
        localProperies = new BeeClientLocalProperties();
        localProperies.load();
        String p1 = (String) localProperies.get("bee.appdef-" + localProperies.getAppName() + ".port");
        String p2 = (String) localProperies.get("bee.appdef-" + localProperies.getAppName() + ".httpPort");
        if (!StringUtil.isBlank(p1)) {
            localProperies.put(Constants.DRPC_PORT, Integer.parseInt(p1));
        }
        if (!StringUtil.isBlank(p2)) {
            localProperies.put(Constants.CONFIG_HTTP_PORT, Integer.parseInt(p2));
        }
        localProperies.put(Constants.CONFIG_APPNAME_KEY, localProperies.getAppName());
    }

    public static BeeClientConfiguration get() {
        return INSTANCE;
    }

    public static BeeClientConfiguration reload() {
        INSTANCE = new BeeClientConfiguration();
        return get();
    }

    public static BeeClientLocalProperties getLocalProperies() {
        return INSTANCE.localProperies;
    }

}
