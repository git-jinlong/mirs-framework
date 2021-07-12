package com.github.mirs.banxiaoxiao.framework.core.woodpecker.enable;

import com.github.mirs.banxiaoxiao.framework.common.util.NetworkUtil;
import com.github.mirs.banxiaoxiao.framework.core.boot.ApplicationInitializer;
import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.config.Constants;
import com.github.mirs.banxiaoxiao.framework.core.woodpecker.vpipe.DrpcCommandProcess;
import com.github.mirs.banxiaoxiao.framework.core.woodpecker.vpipe.DrpcProcessExporter;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

/**
 * @author Administrator
 */
public class WoodpeckerDrpcInitializer implements ApplicationInitializer {

    @Override
    public void init(ConfigurableApplicationContext appContext) throws InitializeException {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) appContext;
        String executorBeanName = registerBean(DrpcCommandProcess.class, appContext);
        BeanDefinitionBuilder builder = rootBeanDefinition(DrpcProcessExporter.class);
        builder.addConstructorArgReference(executorBeanName);
        int drpcPort = Integer.parseInt(appContext.getEnvironment().getProperty(Constants.DRPC_PORT));
        String host = NetworkUtil.getLocalHost() + "_" + drpcPort;
        builder.addConstructorArgValue(host);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        String deskBeanName = executorBeanName + "_drpc";
        registry.registerBeanDefinition(deskBeanName, beanDefinition);
    }
}
