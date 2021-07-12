package com.github.mirs.banxiaoxiao.framework.core.log.enable;

import org.springframework.context.ConfigurableApplicationContext;

import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.boot.ModuleInitializer;
import com.github.mirs.banxiaoxiao.framework.core.log.dlevel.DynamicLevelListener;


/**
 * @author zcy 2020年1月21日
 */
public class DynamicLogLevelInitializer implements ModuleInitializer<LogEnable> {

    @Override
    public void init(ConfigurableApplicationContext appContext) throws InitializeException {
        
    }

    @Override
    public void init(LogEnable enableAnno, ConfigurableApplicationContext appContext) throws InitializeException {
        registerBean(DynamicLevelListener.class, appContext);
    }
}
