package com.github.mirs.banxiaoxiao.framework.core.woodpecker.config;

import org.crsh.plugin.PluginLifeCycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.mirs.banxiaoxiao.framework.core.woodpecker.config.ShellProperties.CrshShellAuthenticationProperties;
import com.github.mirs.banxiaoxiao.framework.core.woodpecker.config.ShellProperties.JaasAuthenticationProperties;
import com.github.mirs.banxiaoxiao.framework.core.woodpecker.config.ShellProperties.KeyAuthenticationProperties;
import com.github.mirs.banxiaoxiao.framework.core.woodpecker.config.ShellProperties.SimpleAuthenticationProperties;

public class CrshConfiguration {

    public static final String AUTH_PREFIX = ShellProperties.SHELL_PREFIX + ".auth";

    @Autowired
    private ShellProperties properties;

    @Bean
    @ConditionalOnMissingBean(PluginLifeCycle.class)
    public CrshBootstrapBean shellBootstrap() {
        CrshBootstrapBean bootstrapBean = new CrshBootstrapBean();
        bootstrapBean.setConfig(this.properties.asCrshShellConfig());
        return bootstrapBean;
    }

    @Configuration
    @Deprecated
    static class CrshAdditionalPropertiesConfiguration {

        @Bean
        @ConditionalOnProperty(prefix = AUTH_PREFIX, name = "type", havingValue = "jaas")
        @ConditionalOnMissingBean(CrshShellAuthenticationProperties.class)
        public JaasAuthenticationProperties jaasAuthenticationProperties() {
            return new JaasAuthenticationProperties();
        }

        @Bean
        @ConditionalOnProperty(prefix = AUTH_PREFIX, name = "type", havingValue = "key")
        @ConditionalOnMissingBean(CrshShellAuthenticationProperties.class)
        public KeyAuthenticationProperties keyAuthenticationProperties() {
            return new KeyAuthenticationProperties();
        }

        @Bean
        @ConditionalOnProperty(prefix = AUTH_PREFIX, name = "type", havingValue = "simple", matchIfMissing = true)
        @ConditionalOnMissingBean(CrshShellAuthenticationProperties.class)
        public SimpleAuthenticationProperties simpleAuthenticationProperties() {
            return new SimpleAuthenticationProperties();
        }
    }
}
