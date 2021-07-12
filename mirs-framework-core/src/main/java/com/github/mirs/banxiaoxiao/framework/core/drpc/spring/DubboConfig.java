package com.github.mirs.banxiaoxiao.framework.core.drpc.spring;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import org.apache.dubbo.config.spring.beans.factory.annotation.ServiceAnnotationBeanPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.config.Constants;

/**
 * @author zcy 2019年3月21日
 */
public class DubboConfig extends ServiceAnnotationBeanPostProcessor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public DubboConfig() {
        super(BeeClientConfiguration.getLocalProperies().getScanPackage());
    }

    @Bean
    public ReferenceAnnotationBeanPostProcessor ReferenceAnnotationBeanPostProcessor() {
        return new ReferenceAnnotationBeanPostProcessor();
    }

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig application = new ApplicationConfig();
        application.setQosEnable(false);
        application.setQosAcceptForeignIp(false);
        application.setName(BeeClientConfiguration.getLocalProperies().getAppName());
        return application;
    }

    @Bean
    public MetadataReportConfig dubboMetadataReportConfig(ConfigurableEnvironment env) {
        MetadataReportConfig mrc = new MetadataReportConfig(env.getProperty(Constants.DRPC_DUBBO_REGISTRY_ADDRESS));
        return mrc;
    }

    @Bean
    public RegistryConfig dubboRegistry(ConfigurableEnvironment env) {
        RegistryConfig rc = new RegistryConfig();
        // dubbo 2.7以上版本 简化注册中心的配置项透传，大部分配置项可能都是服务提供方或者消费方自己需要的配置，无需通过注册中心透传给其他方
        rc.setSimplified(true);
        rc.setTimeout(Integer.parseInt(env.getProperty(Constants.DRPC_TIMEOUT, "30000")));
        rc.setAddress(env.getProperty(Constants.DRPC_DUBBO_REGISTRY_ADDRESS));
        rc.setFile(env.getProperty("user.dir") + "/.dubbo/dubbo-registry.cache");
        rc.setUsername(env.getProperty(Constants.ZOOKEEPER_USERNAME));
        rc.setPassword(env.getProperty(Constants.ZOOKEEPER_PASSWORD));
        logger.debug("create register config , address is {}", rc.getAddress());
        return rc;
    }

    @Bean
    public ProtocolConfig protocolConfig(ConfigurableEnvironment env) {
        ProtocolConfig protocol = new ProtocolConfig();
        if (env.containsProperty(Constants.DRPC_PORT)) {
            protocol.setPort(Integer.parseInt(env.getProperty(Constants.DRPC_PORT)));
        }
        if (env.containsProperty(Constants.DRPC_DUBBO_THREADS)) {
            protocol.setThreads(Integer.parseInt(env.getProperty(Constants.DRPC_DUBBO_THREADS)));
        }
        if (env.containsProperty(Constants.DRPC_DUBBO_PAYLOAD)) {
            protocol.setPayload(Integer.parseInt(env.getProperty(Constants.DRPC_DUBBO_PAYLOAD)));
        }
        if (env.containsProperty(Constants.DRPC_BINDIP)) {
            protocol.setHost(env.getProperty(Constants.DRPC_BINDIP));
        } else if (env.containsProperty("service_bind_ip")) {
            protocol.setHost(env.getProperty("service_bind_ip"));
        }
        protocol.setName("dubbo");
        return protocol;
    }

    @Bean
    public ProviderConfig providerConfig(ConfigurableEnvironment env) {
        ProviderConfig pc = new ProviderConfig();
        if (env.containsProperty(Constants.DRPC_TIMEOUT)) {
            pc.setTimeout(Integer.parseInt(env.getProperty(Constants.DRPC_TIMEOUT)));
        }
        if (env.containsProperty(Constants.DRPC_DUBBO_RETRIES)) {
            pc.setRetries(Integer.parseInt(env.getProperty(Constants.DRPC_DUBBO_RETRIES)));
        }
        if (env.containsProperty(Constants.DRPC_DUBBO_DELAY)) {
            pc.setDelay(Integer.parseInt(env.getProperty(Constants.DRPC_DUBBO_DELAY)));
        }
        if (env.containsProperty(Constants.DRPC_DUBBO_PAYLOAD)) {
            pc.setPayload(Integer.parseInt(env.getProperty(Constants.DRPC_DUBBO_PAYLOAD)));
        }
        pc.setFilter("-exception,drpcException,drpcLog,drpcContext");
        return pc;
    }

    @Bean
    public ConsumerConfig consumerConfig(ConfigurableEnvironment env) {
        ConsumerConfig cc = new ConsumerConfig();
        cc.setLazy(true);
        if (env.containsProperty(Constants.DRPC_DUBBO_CONSUMER_CHECK)) {
            Boolean check = Boolean.parseBoolean(env.getProperty(Constants.DRPC_DUBBO_CONSUMER_CHECK));
            cc.setCheck(check);
        } else {
            cc.setCheck(false);
        }
        if (env.containsProperty(Constants.DRPC_TIMEOUT)) {
            cc.setTimeout(Integer.parseInt(env.getProperty(Constants.DRPC_TIMEOUT)));
        }
        if (env.containsProperty(Constants.DRPC_DUBBO_RETRIES)) {
            cc.setRetries(Integer.parseInt(env.getProperty(Constants.DRPC_DUBBO_RETRIES)));
        }
        cc.setFilter("-exception,drpcException,drpcLog,drpcContext,drpcCross");
        return cc;
    }
}
