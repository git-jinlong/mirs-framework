package com.github.mirs.banxiaoxiao.framework.core.boot;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;

/**
 * @author zcy 2019年8月8日
 */
public class ClassBeanReference extends RuntimeBeanReference {

    private ConfigurableListableBeanFactory factory;

    private Class<?> referneceClass;

    public ClassBeanReference(ConfigurableListableBeanFactory factory, Class<?> referneceClass) {
        super("empty");
        this.factory = factory;
        this.referneceClass = referneceClass;
    }

    @Override
    public String getBeanName() {
        String[] beanNams = factory.getBeanNamesForType(this.referneceClass);
        if (beanNams == null || beanNams.length == 0) {
            throw new BeanDefinitionValidationException("not found instance " + this.referneceClass);
        }
        if (beanNams.length > 1) {
            throw new BeanDefinitionValidationException("multiple instances " + this.referneceClass);
        }
        return beanNams[0];
    }

    @Override
    public Object getSource() {
        return factory.getBean(this.referneceClass);
    }
}
