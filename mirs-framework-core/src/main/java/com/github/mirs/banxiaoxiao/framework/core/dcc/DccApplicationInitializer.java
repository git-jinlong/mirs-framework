package com.github.mirs.banxiaoxiao.framework.core.dcc;

import com.github.mirs.banxiaoxiao.framework.common.util.NetworkUtil;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.boot.ApplicationInitializer;
import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.config.Constants;
import org.apache.zookeeper.KeeperException;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

/**
 * @author zcy 2019年4月15日
 */
public class DccApplicationInitializer implements ApplicationInitializer {

    @Override
    public void init(ConfigurableApplicationContext applicationContext) throws InitializeException {
        DccClient dccClient = tryCreateDccClient();
        SingleDccClientHelper.inject(dccClient);
        applicationContext.getBeanFactory().registerSingleton("dccClient", dccClient);
    }

    private DccClient tryCreateDccClient() throws InitializeException {
        // 先强依赖一下，不能连接zk，则启动失败
        String zkConfigUrl = null;
        try {
            // host不可用的情况下，试一下bee.dcc.zkHost配置项
            String zkhost = BeeClientConfiguration.getLocalProperies().getProperty("bee.dcc.zkHost");
            if (zkhost.startsWith("${")) {
                String key = zkhost.substring(zkhost.indexOf("${") + 2, zkhost.indexOf("}"));
                String realVal = BeeClientConfiguration.getLocalProperies().getProperty(key);
                if (!StringUtil.isBlank(realVal)) {
                    zkhost = realVal;
                }
            }
            if (!isReachableZkHost(zkhost)) {
                throw new InitializeException("can not connet dcc " + zkhost);
            } else {
                zkConfigUrl = zkhost;
            }
            BeeClientConfiguration.getLocalProperies().put(Constants.CONFIG_SERVER_URL_KEY, zkConfigUrl);
            DccClient client = new DccClient(zkConfigUrl);
            return client;
        } catch (IOException | KeeperException | InterruptedException e) {
            throw new InitializeException("connet dcc " + zkConfigUrl + "fail", e);
        }
    }

    private boolean isReachableZkHost(String zkhost) {
        String[] array = zkhost.split(",");
        for (String host : array) {
            if (StringUtil.isBlank(host)) {
                continue;
            }
            host = pickUphost(host);
            if (NetworkUtil.isHostReachable(host)) {
                return true;
            }
        }
        return false;
    }

    private static String pickUphost(String url) {
        String host = url;
        if (url != null && url.contains(":")) {
            host = url.substring(0, url.indexOf(":"));
        }
        return host;
    }

    @Override
    public int order() {
        return ApplicationInitializer.HIGH_ORDER - 1001;
    }
}
