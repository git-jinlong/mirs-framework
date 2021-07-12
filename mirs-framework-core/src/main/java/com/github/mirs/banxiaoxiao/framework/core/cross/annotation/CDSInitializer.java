package com.github.mirs.banxiaoxiao.framework.core.cross.annotation;

import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.boot.ModuleInitializer;
import com.github.mirs.banxiaoxiao.framework.core.cross.config.CDSReferenceBeanFactory;
import com.github.mirs.banxiaoxiao.framework.core.cross.config.CDServiceBeanFactory;
import org.apache.dubbo.config.spring.util.BeanRegistrar;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.annotation.Annotation;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

/**
 * @Auther: lxj
 * @Date: 2020/3/23 15:15
 * @Description:
 */
public class CDSInitializer implements ModuleInitializer {
    @Override
    public void init(Annotation enableAnno, ConfigurableApplicationContext appContext) throws InitializeException {

    }

    @Override
    public void init(ConfigurableApplicationContext appContext) throws InitializeException {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) appContext;
        String[] basePackages = getScanPackages(appContext);
        registerCDServiceBeanFactory(basePackages, registry);
        registerDReferenceBeanFactory(registry);
    }

    //注册cds
    private void registerCDServiceBeanFactory(String[] packagesToScan, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = rootBeanDefinition(CDServiceBeanFactory.class);
        builder.addConstructorArgValue(packagesToScan);
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
    }

    private void registerDReferenceBeanFactory(BeanDefinitionRegistry registry) {
        BeanRegistrar.registerInfrastructureBean(registry, CDSReferenceBeanFactory.BEAN_NAME, CDSReferenceBeanFactory.class);
    }
}
