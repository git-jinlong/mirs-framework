package com.github.mirs.banxiaoxiao.framework.swagger.enable;

import com.github.mirs.banxiaoxiao.framework.core.boot.ApplicationInitializer;
import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.swagger.config.SwaggerConfig;
import com.github.mirs.banxiaoxiao.framework.swagger.config.SwaggerWebMvcConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: bc
 * @date: 2021-03-03 14:10
 **/
public class SwaggerInitializer implements ApplicationInitializer {

    @Override
    public void init(ConfigurableApplicationContext applicationContext) throws InitializeException {

        registerBean(SwaggerConfig.class, applicationContext);

        registerBean(SwaggerWebMvcConfiguration.class, applicationContext);
    }
}
