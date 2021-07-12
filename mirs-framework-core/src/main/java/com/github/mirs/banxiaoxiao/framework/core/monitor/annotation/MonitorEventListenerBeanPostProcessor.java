package com.github.mirs.banxiaoxiao.framework.core.monitor.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.mirs.banxiaoxiao.framework.core.monitor.EventListener;
import com.github.mirs.banxiaoxiao.framework.core.monitor.EventPublishHelper;
import com.github.mirs.banxiaoxiao.framework.core.monitor.app.AppMonitor;

public class MonitorEventListenerBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
    
    private ApplicationContext applicationContext;
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof EventListener) {
            AppMonitor appMonitor = this.applicationContext.getBean(AppMonitor.class);
            if(appMonitor != null) {
                appMonitor.addEventListener((EventListener)bean);
            }
            EventPublishHelper.single().addEventListener((EventListener)bean);
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
