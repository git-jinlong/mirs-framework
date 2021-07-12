/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mirs.banxiaoxiao.framework.core.drpc.spring.annotation;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import org.apache.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import org.apache.dubbo.config.spring.beans.factory.annotation.ServiceAnnotationBeanPostProcessor;
import org.apache.dubbo.config.spring.util.BeanRegistrar;
import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.drpc.p2p.P2PServiceBeanFactory;
import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.DReferenceBeanFactory;
import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.DServiceBeanFactory;
import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.DubboConfig;

public class DrpcEnableRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Set<String> packagesToScan = getPackagesToScan(importingClassMetadata);
        registerDubboConfig(registry);
        registerDServiceBeanFactory(packagesToScan, registry);
        registerP2PServiceBeanFactory(packagesToScan, registry);
        registerDReferenceBeanFactory(registry);
    }

    /**
     * Registers {@link ServiceAnnotationBeanPostProcessor}
     *
     * @param packagesToScan
     *            packages to scan without resolving placeholders
     * @param registry
     *            {@link BeanDefinitionRegistry}
     * @since 2.5.8
     */
    private void registerDServiceBeanFactory(Set<String> packagesToScan, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = rootBeanDefinition(DServiceBeanFactory.class);
        builder.addConstructorArgValue(packagesToScan);
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
    }

    /**
     * Registers {@link ReferenceAnnotationBeanPostProcessor} into {@link BeanFactory}
     *
     * @param registry
     *            {@link BeanDefinitionRegistry}
     */
    private void registerDReferenceBeanFactory(BeanDefinitionRegistry registry) {
        BeanRegistrar.registerInfrastructureBean(registry, DReferenceBeanFactory.BEAN_NAME, DReferenceBeanFactory.class);
    }

    private void registerDubboConfig(BeanDefinitionRegistry registry) {
        BeanRegistrar.registerInfrastructureBean(registry, DubboConfig.class.getName(), DubboConfig.class);
    }

    private void registerP2PServiceBeanFactory(Set<String> packagesToScan, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = rootBeanDefinition(P2PServiceBeanFactory.class);
        builder.addConstructorArgValue(packagesToScan);
        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
    }

    private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
        String[] basePackages = BeeClientConfiguration.getLocalProperies().getScanPackage().split(",");
        Set<String> packagesToScan = new LinkedHashSet<String>(basePackages.length);
        for (String basePackage : basePackages) {
            if (StringUtils.hasText(basePackage)) {
                String resolvedPackageToScan = environment.resolvePlaceholders(basePackage.trim());
                packagesToScan.add(resolvedPackageToScan);
            }
        }
        return packagesToScan;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
