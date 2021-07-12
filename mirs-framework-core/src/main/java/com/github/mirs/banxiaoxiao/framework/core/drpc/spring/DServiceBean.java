package com.github.mirs.banxiaoxiao.framework.core.drpc.spring;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConfigCenterConfig;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.ModuleConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.extension.SpringExtensionFactory;

import com.github.mirs.banxiaoxiao.framework.core.drpc.DServiceConfig;
import com.github.mirs.banxiaoxiao.framework.core.drpc.annotation.DS;

/**
 * @author zcy 2019年3月21日
 */
public class DServiceBean<T> extends DServiceConfig<T> implements InitializingBean, DisposableBean, ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent>, BeanNameAware {

    /** */
    private static final long serialVersionUID = 5187999537147910663L;

    private static transient ApplicationContext SPRING_CONTEXT;

    private transient ApplicationContext applicationContext;

    private transient String beanName;

    private transient boolean supportedApplicationListener;

    public DServiceBean() {
        super();
    }

    public DServiceBean(DS service) {
        super(service);
    }

    public static ApplicationContext getSpringContext() {
        return SPRING_CONTEXT;
    }
    
    public void setInterfaceClass(Class<?> interfaceClass) {
        super.setInterface(interfaceClass);
    }

    public void setConfig(String config) {
        super.setRpcConfig(config);
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        SpringExtensionFactory.addApplicationContext(applicationContext);
        if (applicationContext != null) {
            SPRING_CONTEXT = applicationContext;
            try {
                Method method = applicationContext.getClass().getMethod("addApplicationListener", new Class<?>[] { ApplicationListener.class }); // backward
                                                                                                                                                 // compatibility
                                                                                                                                                 // to
                                                                                                                                                 // spring
                                                                                                                                                 // 2.0.1
                method.invoke(applicationContext, new Object[] { this });
                supportedApplicationListener = true;
            } catch (Throwable t) {
                if (applicationContext instanceof AbstractApplicationContext) {
                    try {
                        Method method = AbstractApplicationContext.class.getDeclaredMethod("addListener",
                                new Class<?>[] { ApplicationListener.class }); // backward compatibility to spring 2.0.1
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                        }
                        method.invoke(applicationContext, new Object[] { this });
                        supportedApplicationListener = true;
                    } catch (Throwable t2) {
                    }
                }
            }
        }
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (isDelay() && !getConfig().isExported() && !getConfig().isUnexported()) {
            if (logger.isInfoEnabled()) {
                logger.info("The service ready on spring started. service: " + getConfig().getInterface());
            }
            export();
        }
    }

    private boolean isDelay() {
        Integer delay = getConfig().getDelay();
        ProviderConfig provider = getConfig().getProvider();
        if (delay == null && provider != null) {
            delay = provider.getDelay();
        }
        return supportedApplicationListener && (delay == null || delay == -1);
    }

    @Override
    @SuppressWarnings({ "deprecation" })
    public void afterPropertiesSet() throws Exception {
        if (getConfig().getProvider() == null) {
            Map<String, ProviderConfig> providerConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(
                    applicationContext, ProviderConfig.class, false, false);
            if (providerConfigMap != null && providerConfigMap.size() > 0) {
                Map<String, ProtocolConfig> protocolConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(
                        applicationContext, ProtocolConfig.class, false, false);
                if ((protocolConfigMap == null || protocolConfigMap.size() == 0) && providerConfigMap.size() > 1) { // backward compatibility
                    List<ProviderConfig> providerConfigs = new ArrayList<ProviderConfig>();
                    for (ProviderConfig config : providerConfigMap.values()) {
                        if (config.isDefault() != null && config.isDefault().booleanValue()) {
                            providerConfigs.add(config);
                        }
                    }
                    if (!providerConfigs.isEmpty()) {
                        getConfig().setProviders(providerConfigs);
                    }
                } else {
                    ProviderConfig providerConfig = null;
                    for (ProviderConfig config : providerConfigMap.values()) {
                        if (config.isDefault() == null || config.isDefault().booleanValue()) {
                            if (providerConfig != null) {
                                throw new IllegalStateException("Duplicate provider configs: " + providerConfig + " and " + config);
                            }
                            providerConfig = config;
                        }
                    }
                    if (providerConfig != null) {
                        getConfig().setProvider(providerConfig);
                    }
                }
            }
        }
        if (getConfig().getApplication() == null && (getConfig().getProvider() == null || getConfig().getProvider().getApplication() == null)) {
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
        if (getConfig().getModule() == null && (getConfig().getProvider() == null || getConfig().getProvider().getModule() == null)) {
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
                && (getConfig().getProvider() == null || getConfig().getProvider().getRegistries() == null || getConfig().getProvider()
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
        if (getConfig().getMonitor() == null && (getConfig().getProvider() == null || getConfig().getProvider().getMonitor() == null)
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
        if ((getConfig().getProtocols() == null || getConfig().getProtocols().isEmpty())
                && (getConfig().getProvider() == null || getConfig().getProvider().getProtocols() == null || getConfig().getProvider().getProtocols()
                        .isEmpty())) {
            Map<String, ProtocolConfig> protocolConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(
                    applicationContext, ProtocolConfig.class, false, false);
            if (protocolConfigMap != null && protocolConfigMap.size() > 0) {
                List<ProtocolConfig> protocolConfigs = new ArrayList<ProtocolConfig>();
                for (ProtocolConfig config : protocolConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        protocolConfigs.add(config);
                    }
                }
                if (protocolConfigs != null && !protocolConfigs.isEmpty()) {
                    getConfig().setProtocols(protocolConfigs);
                }
            }
        }
        if (getConfig().getPath() == null || getConfig().getPath().length() == 0) {
            if (beanName != null && beanName.length() > 0 && getConfig().getInterface() != null && getConfig().getInterface().length() > 0
                    && beanName.startsWith(getConfig().getInterface())) {
                getConfig().setPath(beanName);
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

        if (!isDelay()) {
            export();
        }
    }

    @Override
    public void destroy() throws Exception {
        // This will only be called for singleton scope bean, and expected to be called by spring shutdown hook when BeanFactory/ApplicationContext
        // destroys.
        // We will guarantee dubbo related resources being released with dubbo shutdown hook.
        // unexport();
    }
}
