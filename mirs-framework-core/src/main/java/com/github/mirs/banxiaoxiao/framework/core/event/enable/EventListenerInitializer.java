package com.github.mirs.banxiaoxiao.framework.core.event.enable;

import java.lang.annotation.Annotation;

import org.springframework.context.ConfigurableApplicationContext;

import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.boot.ModuleInitializer;
import com.github.mirs.banxiaoxiao.framework.core.event.EventPublishHelper;

/**
 * @author zcy 2019年7月4日
 */
public class EventListenerInitializer implements ModuleInitializer {

    @Override
    public void init(ConfigurableApplicationContext appContext) throws InitializeException {
        registerBean(EventListenerBeanPostProcessor.class, appContext);
        appContext.getBeanFactory().registerSingleton("eventPublisher", EventPublishHelper.get());
    }

    @Override
    public void init(Annotation enableAnno, ConfigurableApplicationContext appContext) throws InitializeException {
    }
}
