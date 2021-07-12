package com.github.mirs.banxiaoxiao.framework.core.monitor.annotation;

import org.springframework.context.annotation.Bean;

import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;
import com.github.mirs.banxiaoxiao.framework.core.monitor.app.AppMonitor;
import com.github.mirs.banxiaoxiao.framework.core.monitor.app.ClientQuery;

public class AppMonitorConfig {

    @Bean
    public MonitorEventListenerBeanPostProcessor monitorEventListenerBeanPostProcessor() {
        return new MonitorEventListenerBeanPostProcessor();
    }

    @Bean
    public AppMonitor appMonitor(DccClient dccClient) {
        return new AppMonitor(dccClient);
    }

    @Bean
    public ClientQuery dccClientQuery(DccClient dccClient) {
        return new ClientQuery(dccClient);
    }
}
