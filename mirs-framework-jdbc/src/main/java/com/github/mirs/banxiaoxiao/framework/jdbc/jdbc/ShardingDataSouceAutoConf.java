package com.github.mirs.banxiaoxiao.framework.jdbc.jdbc;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.jdbc.jdbc.sharding.PropertyUtil;
import com.github.mirs.banxiaoxiao.framework.jdbc.jdbc.sharding.SpringBootMasterSlaveRuleConfigurationProperties;
import com.github.mirs.banxiaoxiao.framework.jdbc.jdbc.sharding.SpringBootShardingRuleConfigurationProperties;
import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariDataSource;
import io.shardingsphere.api.config.ShardingRuleConfiguration;
import io.shardingsphere.core.exception.ShardingException;
import io.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import io.shardingsphere.shardingjdbc.util.DataSourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zcy 2018年10月16日
 */
@EnableConfigurationProperties({DataSourceProperties.class, SpringBootShardingRuleConfigurationProperties.class,
        SpringBootMasterSlaveRuleConfigurationProperties.class})
public class ShardingDataSouceAutoConf implements EnvironmentAware {

    @Autowired
    DataSourceProperties commonProperties;

    @Autowired
    private SpringBootShardingRuleConfigurationProperties shardingProperties;

    @Autowired
    private SpringBootMasterSlaveRuleConfigurationProperties masterSlaveProperties;

    @Autowired(required = false)
    private ShardingRuleConfiguration shardingRuleConfiguration;

    private final Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();

    /**
     * Get data source bean.
     *
     * @return data source bean
     * @throws SQLException SQL exception
     */
    @Bean
    public DataSource dataSource() throws SQLException {
        HikariDataSource dataSource = null;
        if (!StringUtil.isBlank(commonProperties.getDriverClassName())) {
            dataSource = new HikariDataSource();
            if (commonProperties.getDriverClassName() != null)
                dataSource.setDriverClassName(commonProperties.getDriverClassName());
            if (commonProperties.getUrl() != null)
                dataSource.setJdbcUrl(commonProperties.getUrl());
            if (commonProperties.getUsername() != null)
                dataSource.setUsername(commonProperties.getUsername());
            if (commonProperties.getPassword() != null)
                dataSource.setPassword(commonProperties.getPassword());
            if (commonProperties.getValidationQuery() != null)
                dataSource.setConnectionTestQuery(commonProperties.getValidationQuery());
            if (commonProperties.getMaxActive() != null)
                dataSource.setMaximumPoolSize(commonProperties.getMaxActive());
            if (commonProperties.getMaxIdle() != null)
                dataSource.setMinimumIdle(commonProperties.getMaxIdle());
            if (commonProperties.getMaxWait() != null)
                dataSource.setConnectionTimeout(commonProperties.getMaxWait());
        }
        if (dataSource != null) {
            dataSourceMap.put("commonHikarDataSource", dataSource);
        }
        if (shardingRuleConfiguration == null) {
            shardingRuleConfiguration = shardingProperties.getShardingRuleConfiguration();
        }
        return null == masterSlaveProperties.getMasterDataSourceName() ? ShardingDataSourceFactory.createDataSource(dataSourceMap,
                shardingRuleConfiguration, shardingProperties.getConfigMap(), shardingProperties.getProps()) : MasterSlaveDataSourceFactory
                .createDataSource(dataSourceMap, masterSlaveProperties.getMasterSlaveRuleConfiguration(), masterSlaveProperties.getConfigMap(),
                        masterSlaveProperties.getProps());
    }

    @Override
    public final void setEnvironment(final Environment environment) {
        setDataSourceMap(environment);
    }

    @SuppressWarnings("unchecked")
    private void setDataSourceMap(final Environment environment) {
        String prefix = "sharding.jdbc.datasource.";
        String dataSources = environment.getProperty(prefix + "names");
        if (dataSources == null) {
            return;
        }
        for (String each : dataSources.split(",")) {
            try {
                Map<String, Object> dataSourceProps = PropertyUtil.handle(environment, prefix + each, Map.class);
                Preconditions.checkState(!dataSourceProps.isEmpty(), "Wrong datasource properties!");
                DataSource dataSource = DataSourceUtil.getDataSource(dataSourceProps.get("type").toString(), dataSourceProps);
                dataSourceMap.put(each, dataSource);
            } catch (final ReflectiveOperationException ex) {
                throw new ShardingException("Can't find datasource type!", ex);
            }
        }
    }
}
