package com.github.mirs.banxiaoxiao.framework.jdbc.jdbc;

import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author zcy 2018年10月16日
 */
@EnableConfigurationProperties({ DataSourceProperties.class, SlaveDataSourceProperties.class, MasterDataSourceProperties.class })
public class CommonDataSouceAutoConf {

    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider(MasterDataSourceProperties properties, SlaveDataSourceProperties twoproperties) {
        return new TwoJdbcDataSourceProvider(properties, twoproperties);
    }
}
