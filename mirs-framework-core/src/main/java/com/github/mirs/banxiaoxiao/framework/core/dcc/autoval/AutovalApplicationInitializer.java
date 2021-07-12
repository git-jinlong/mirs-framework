package com.github.mirs.banxiaoxiao.framework.core.dcc.autoval;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import com.github.mirs.banxiaoxiao.framework.core.boot.ApplicationInitializer;
import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.config.Constants;
import com.github.mirs.banxiaoxiao.framework.core.dcc.AbstractDcc;
import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;
import com.github.mirs.banxiaoxiao.framework.core.dcc.SingleDccClientHelper;
import com.github.mirs.banxiaoxiao.framework.core.dcc.autoval.spring.SpringValueAnnotationSpace;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

/**
 * @author zcy 2019年4月15日
 */
public class AutovalApplicationInitializer extends AbstractDcc implements ApplicationInitializer {

    @Override
    public void init(ConfigurableApplicationContext applicationContext) throws InitializeException {
        boolean enableDcc = false;
        try {
            enableDcc = SingleDccClientHelper.get() != null;
        } catch (NullPointerException e) {
            enableDcc = false;
        }
        if (enableDcc) {
            // 初始化vals
            DccClient dccClient = super.getDccClient();
            try {
                Vals.init(dccClient);
            } catch (IOException e) {
                throw new InitializeException("init value space fail", e);
            }
        }
        // 将配置信息注入到spring context
        String appName = BeeClientConfiguration.getLocalProperies().getAppName();
        CompositePropertySource cps = new CompositePropertySource(BeeClientConfiguration.getLocalProperies().getAppName() + "composite");
        for (ValSpace valspace : Vals.getValSpaces()) {
            PropertiesPropertySource ps = new PropertiesPropertySource(valspace.getNamespace(), valspace);
            cps.addFirstPropertySource(ps);
        }
        if (!enableDcc) {
            PropertiesPropertySource ps = new PropertiesPropertySource(appName, BeeClientConfiguration.getLocalProperies());
            cps.addFirstPropertySource(ps);
        }
        Properties defaultProperties = new Properties();
        PropertiesPropertySource defaultps = new PropertiesPropertySource(appName + "default", defaultProperties);
        String p1 = (String) cps.getProperty("bee.appdef-" + appName + ".port");
        String p2 = (String) cps.getProperty("bee.appdef-" + appName + ".httpPort");
        if (!StringUtil.isBlank(p1)) {
            defaultProperties.put(Constants.DRPC_PORT, Integer.parseInt(p1));
        }
        if (!StringUtil.isBlank(p2)) {
            defaultProperties.put(Constants.CONFIG_HTTP_PORT, Integer.parseInt(p2));
        }
        cps.addFirstPropertySource(defaultps);
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        environment.getPropertySources().addFirst(cps);
        // 将SpringValueAnnotationSpace交付给spring bean factory托管
        SpringValueAnnotationSpace svas = (SpringValueAnnotationSpace) Vals.getValSpace(SpringValueAnnotationSpace.namespace());
        applicationContext.getBeanFactory().registerSingleton("springValueAnnotationSpace", svas);
    }

    @Override
    public int order() {
        return ApplicationInitializer.HIGH_ORDER - 1000;
    }
}
