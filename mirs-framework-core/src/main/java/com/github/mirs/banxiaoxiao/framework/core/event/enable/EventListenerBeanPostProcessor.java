package com.github.mirs.banxiaoxiao.framework.core.event.enable;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.github.mirs.banxiaoxiao.framework.core.event.BizInterceptor;
import com.github.mirs.banxiaoxiao.framework.core.event.EventListener;
import com.github.mirs.banxiaoxiao.framework.core.event.EventPublishHelper;
import com.github.mirs.banxiaoxiao.framework.core.event.EventPublisher;

public class EventListenerBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
    
    private ApplicationContext applicationContext;
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof EventListener) {
            EventListener<?> listener = (EventListener<?>) bean;
            EventPublishHelper.get().addEventListener(listener);
            Map<String, EventPublisher> publishers = this.applicationContext.getBeansOfType(EventPublisher.class);
            if (publishers != null) {
                for (EventPublisher publisher : publishers.values()) {
                    publisher.addEventListener(listener);
                }
            }
        }
        if (bean instanceof BizInterceptor) {
            BizInterceptor<?> interceptor = (BizInterceptor<?>) bean;
            EventPublishHelper.get().addBizInterceptor(interceptor);
            Map<String, EventPublisher> publishers = this.applicationContext.getBeansOfType(EventPublisher.class);
            if (publishers != null) {
                for (EventPublisher publisher : publishers.values()) {
                    publisher.addBizInterceptor(interceptor);
                }
            }
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}