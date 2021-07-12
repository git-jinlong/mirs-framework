package com.github.mirs.banxiaoxiao.framework.core.drpc.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.mirs.banxiaoxiao.framework.core.cross.annotation.CDS;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConfigCenterConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.ModuleConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.extension.SpringExtensionFactory;
import org.apache.dubbo.config.support.Parameter;

import com.github.mirs.banxiaoxiao.framework.core.drpc.DReferenceConfig;
import com.github.mirs.banxiaoxiao.framework.core.drpc.annotation.DS;
import com.github.mirs.banxiaoxiao.framework.core.spring.SpringContextHolder;

/**
 * @author zcy 2019年3月21日
 */
public class DReferenceBean<T> extends DReferenceConfig<T> implements FactoryBean<T>, ApplicationContextAware, InitializingBean, DisposableBean {

    /** */
    private static final long serialVersionUID = 5187999537147910663L;

    private transient ApplicationContext applicationContext;

    public DReferenceBean() {
        super();
    }

    public DReferenceBean(DS reference) {
        super(reference);
    }

    public DReferenceBean(CDS reference) {
        super(reference);
    }

    public DReferenceBean(String config, Class<?> interfaceClass, String group, Class<?> groupLoader) {
        super(config, interfaceClass, group, groupLoader);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        SpringExtensionFactory.addApplicationContext(applicationContext);
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        super.setInterface(interfaceClass);
    }

    @Override
    public T getObject() throws Exception {
        return get();
    }

    @Override
    public Class<?> getObjectType() {
        return getConfig().getInterfaceClass();
    }

    public void setConfig(String config) {
        super.setRpcConfig(config);
    }
    
    @Override
    @Parameter(excluded = true)
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(this.applicationContext == null) {
            this.applicationContext = SpringContextHolder.get();
        }
        if (getConfig().getConsumer() == null) {
            Map<String, ConsumerConfig> consumerConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(
                    applicationContext, ConsumerConfig.class, false, false);
            if (consumerConfigMap != null && consumerConfigMap.size() > 0) {
                ConsumerConfig consumerConfig = null;
                for (ConsumerConfig config : consumerConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        if (consumerConfig != null) {
                            throw new IllegalStateException("Duplicate consumer configs: " + consumerConfig + " and " + config);
                        }
                        consumerConfig = config;
                    }
                }
                if (consumerConfig != null) {
                    getConfig().setConsumer(consumerConfig);
                }
            }
        }
        if (getConfig().getApplication() == null && (getConfig().getConsumer() == null || getConfig().getConsumer().getApplication() == null)) {
            Map<String, ApplicationConfig> applicationConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(
                    applicationContext, ApplicationConfig.class, false, false);
            if (applicationConfigMap != null && applicationConfigMap.size() > 0) {
                ApplicationConfig applicationConfig = null;
                for (ApplicationConfig config : applicationConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        if (applicationConfig != null) {
                            throw new IllegalStateException("Duplicate application configs: " + applicationConfig + " and " + config);
                        }
                        applicationConfig = config;
                    }
                }
                if (applicationConfig != null) {
                    getConfig().setApplication(applicationConfig);
                }
            }
        }
        if (getConfig().getModule() == null && (getConfig().getConsumer() == null || getConfig().getConsumer().getModule() == null)) {
            Map<String, ModuleConfig> moduleConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(
                    applicationContext, ModuleConfig.class, false, false);
            if (moduleConfigMap != null && moduleConfigMap.size() > 0) {
                ModuleConfig moduleConfig = null;
                for (ModuleConfig config : moduleConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        if (moduleConfig != null) {
                            throw new IllegalStateException("Duplicate module configs: " + moduleConfig + " and " + config);
                        }
                        moduleConfig = config;
                    }
                }
                if (moduleConfig != null) {
                    getConfig().setModule(moduleConfig);
                }
            }
        }
        if ((getConfig().getRegistries() == null || getConfig().getRegistries().isEmpty())
                && (getConfig().getConsumer() == null || getConfig().getConsumer().getRegistries() == null || getConfig().getConsumer()
                        .getRegistries().isEmpty())
                && (getConfig().getApplication() == null || getConfig().getApplication().getRegistries() == null || getConfig().getApplication()
                        .getRegistries().isEmpty())) {
            Map<String, RegistryConfig> registryConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(
                    applicationContext, RegistryConfig.class, false, false);
            if (registryConfigMap != null && registryConfigMap.size() > 0) {
                List<RegistryConfig> registryConfigs = new ArrayList<RegistryConfig>();
                for (RegistryConfig config : registryConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        registryConfigs.add(config);
                    }
                }
                if (registryConfigs != null && !registryConfigs.isEmpty()) {
                    getConfig().setRegistries(registryConfigs);
                }
            }
        }
        if (getConfig().getMonitor() == null && (getConfig().getConsumer() == null || getConfig().getConsumer().getMonitor() == null)
                && (getConfig().getApplication() == null || getConfig().getApplication().getMonitor() == null)) {
            Map<String, MonitorConfig> monitorConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(
                    applicationContext, MonitorConfig.class, false, false);
            if (monitorConfigMap != null && monitorConfigMap.size() > 0) {
                MonitorConfig monitorConfig = null;
                for (MonitorConfig config : monitorConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        if (monitorConfig != null) {
                            throw new IllegalStateException("Duplicate monitor configs: " + monitorConfig + " and " + config);
                        }
                        monitorConfig = config;
                    }
                }
                if (monitorConfig != null) {
                    getConfig().setMonitor(monitorConfig);
                }
            }
        }
        if (getConfig().getMetadataReportConfig() == null) {
            Map<String, MetadataReportConfig> metadataReportConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, MetadataReportConfig.class, false, false);
            if (metadataReportConfigMap != null && metadataReportConfigMap.size() == 1) {
                getConfig().setMetadataReportConfig(metadataReportConfigMap.values().iterator().next());
            } else if (metadataReportConfigMap != null && metadataReportConfigMap.size() > 1) {
                throw new IllegalStateException("Multiple MetadataReport configs: " + metadataReportConfigMap);
            }
        }

        if (getConfig().getConfigCenter() == null) {
            Map<String, ConfigCenterConfig> configenterMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ConfigCenterConfig.class, false, false);
            if (configenterMap != null && configenterMap.size() == 1) {
                getConfig().setConfigCenter(configenterMap.values().iterator().next());
            } else if (configenterMap != null && configenterMap.size() > 1) {
                throw new IllegalStateException("Multiple ConfigCenter found:" + configenterMap);
            }
        }
        Boolean b = getConfig().isInit();
        if (b == null && getConfig().getConsumer() != null) {
            b = getConfig().getConsumer().isInit();
        }
        if (b != null && b.booleanValue()) {
            getObject();
        }
    }

}
