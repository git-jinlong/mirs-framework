package com.github.mirs.banxiaoxiao.framework.core.drpc.spring.annotation;

import com.github.mirs.banxiaoxiao.framework.core.boot.ApplicationInitializer;
import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.DubboConfig;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: bc
 * @date: 2021-05-25 11:11
 **/
public class DrpcInitializer implements ApplicationInitializer {
    @Override
    public void init(ConfigurableApplicationContext appContext) throws InitializeException {
        this.registerBean(DubboConfig.class,appContext);
    }
}
