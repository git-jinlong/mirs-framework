package com.github.mirs.banxiaoxiao.framework.core.monitor;

import com.github.mirs.banxiaoxiao.framework.common.util.NetworkUtil;
import com.github.mirs.banxiaoxiao.framework.core.boot.ApplicationInitializer;
import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.config.Constants;
import com.github.mirs.banxiaoxiao.framework.core.dcc.AbstractDcc;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.core.monitor.app.ClientRegister;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author zcy 2019年4月15日
 */
public class MonitorApplicationInitializer extends AbstractDcc implements ApplicationInitializer {

    @Override
    public void init(ConfigurableApplicationContext applicationContext) throws InitializeException {
        String appName = BeeClientConfiguration.getLocalProperies().getAppName();
        String localHost = null;
        if (applicationContext.getEnvironment().containsProperty(Constants.DRPC_BINDIP)) {
            localHost = applicationContext.getEnvironment().getProperty(Constants.DRPC_BINDIP);
        } else {
            localHost = NetworkUtil.getLocalHost();
        }
        int drpcPort = Integer.parseInt(applicationContext.getEnvironment().getProperty(Constants.DRPC_PORT));
        int httpPort = Integer.parseInt(applicationContext.getEnvironment().getProperty(Constants.CONFIG_HTTP_PORT));
        ClientRegister register = new ClientRegister(super.getDccClient());
        register.registeApp(appName, localHost, drpcPort, httpPort);
        TComLogs.info(BeeClientConfiguration.getLocalProperies().getAppName() + " started, rpc port {}, http port {}", drpcPort, httpPort);
    }
}
