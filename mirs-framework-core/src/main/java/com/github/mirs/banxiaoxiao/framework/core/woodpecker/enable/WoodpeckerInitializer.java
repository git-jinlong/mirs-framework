package com.github.mirs.banxiaoxiao.framework.core.woodpecker.enable;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;

import org.crsh.spring.SpringBootstrap;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

import com.github.mirs.banxiaoxiao.framework.core.boot.ApplicationInitializer;
import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.woodpecker.cmd.JvmCommandResolver;
import com.github.mirs.banxiaoxiao.framework.core.woodpecker.config.CrshConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.woodpecker.config.ShellProperties;

/**
 * @author Administrator
 *
 */
public class WoodpeckerInitializer implements ApplicationInitializer {
	
	@Override
	public void init(ConfigurableApplicationContext appContext) throws InitializeException {
		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) appContext;
		Properties config = new Properties();
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(0);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		int port = serverSocket.getLocalPort();
		config.put("crash.vfs.refresh_period", 1);
		config.put("ssh.port", port);
//		config.put("crash.ssh.auth_timeout", 300000);
//		config.put("crash.ssh.idle_timeout", 300000);
//		config.put("crash.auth", "simple");
//		config.put("crash.auth.simple.username", "test");
//		config.put("crash.auth.simple.password", "test");
//		BeanDefinitionBuilder builder = rootBeanDefinition(SpringBootstrap.class);
////	    builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
//	    builder.addPropertyValue("config", config);
//	    builder.addPropertyValue("cmdMountPointConfig", "file:configs/commands");
//	    AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
//	    String beanName = new AnnotationBeanNameGenerator().generateBeanName(beanDefinition, registry);
//	    registry.registerBeanDefinition(beanName, beanDefinition);
	    
	    registerBean(CrshConfiguration.class, appContext);
        registerBean(ShellProperties.class, appContext);
        registerBean(JvmCommandResolver.class, appContext);
	}
	
}
