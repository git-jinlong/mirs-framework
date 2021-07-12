package com.github.mirs.banxiaoxiao.framework.jdbc.ibatis;

import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.boot.ModuleInitializer;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * 提供注入Mapper的能力
 *
 * @author zcy 2019年11月15日
 */
public interface IbatisApplicationInitializer<T extends Annotation> extends ModuleInitializer<T> {

    default public void registerMapperBean(Package scanPackage, ConfigurableApplicationContext appContext) {
        registerMapperBeanByPackage(scanPackage.getName(), appContext);
    }

    default public void registerMapperBeanByPackage(String packageName, ConfigurableApplicationContext appContext) throws InitializeException {
        String basePackage = ClassUtils.convertClassNameToResourcePath(appContext.getEnvironment().resolveRequiredPlaceholders(packageName));
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + basePackage + "/*.class";
        try {
            Resource[] resources = appContext.getResources(packageSearchPath);
            MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                registerMapperBean(className, appContext);
            }
        } catch (IOException e) {
            throw new InitializeException("register package bean fail", e);
        }
    }

    default public void registerMapperBean(Class<?> clazz, ConfigurableApplicationContext appContext) {
        registerMapperBean(clazz.getName(), appContext);
    }

    default public void registerMapperBean(String clazzName, ConfigurableApplicationContext appContext) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) appContext;
        try {
            SimpleMetadataReaderFactory factory = new SimpleMetadataReaderFactory(getClass().getClassLoader());
            MetadataReader metadtaReader = factory.getMetadataReader(clazzName);
            ScannedGenericBeanDefinition definition = new ScannedGenericBeanDefinition(metadtaReader);
            String beanName = beanNameGenerator.generateBeanName(definition, registry);
            registry.registerBeanDefinition(beanName, definition);
            definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName()); // issue #59
            definition.setBeanClass(MapperFactoryBean.class);
            definition.getPropertyValues().add("addToConfig", true);
            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        } catch (Exception ex) {
            throw new InitializeException("Could not determine auto-configuration " + "package, automatic mapper scanning disabled.");
        }
    }

}
