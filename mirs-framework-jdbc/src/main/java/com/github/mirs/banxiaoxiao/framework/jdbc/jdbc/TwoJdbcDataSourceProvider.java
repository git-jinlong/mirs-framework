package com.github.mirs.banxiaoxiao.framework.jdbc.jdbc;

import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class TwoJdbcDataSourceProvider implements DynamicDataSourceProvider {

    private final DataSourceProperties one;

    private final DataSourceProperties two;

    public TwoJdbcDataSourceProvider(DataSourceProperties properties1, DataSourceProperties properties2) {
        this.one = properties1;
        this.two = properties2;
    }

    @Override
    public Map<String, DataSource> loadDataSources() {
        BackupDataSource oneDs = null;
        BackupDataSource towDs = null;
        Map<String, DataSource> dataSourceMap = new HashMap<>(2);
        if (one != null && !StringUtil.isBlank(one.getUrl())) {
            oneDs = createDataSource(one);
            dataSourceMap.put("master", oneDs);
        }
        if (two != null && !StringUtil.isBlank(two.getUrl())) {
            towDs = createDataSource(two);
            dataSourceMap.put("slave", towDs);
        }
        if (oneDs != null && towDs != null) {
            oneDs.setBackup(towDs);
            towDs.setBackup(oneDs);
        }
        return dataSourceMap;
    }

    public BackupDataSource createDataSource(DataSourceProperties properties) {
        BackupDataSource dataSource = new BackupDataSource();
        if (properties.getDriverClassName() != null)
            dataSource.setDriverClassName(properties.getDriverClassName());
        if (properties.getUrl() != null)
            dataSource.setJdbcUrl(properties.getUrl());
        if (properties.getUsername() != null)
            dataSource.setUsername(properties.getUsername());
        if (properties.getPassword() != null)
            dataSource.setPassword(properties.getPassword());
        if (properties.getValidationQuery() != null)
            dataSource.setConnectionTestQuery(properties.getValidationQuery());
        if (properties.getMaxActive() != null)
            dataSource.setMaximumPoolSize(properties.getMaxActive());
        if (properties.getMaxIdle() != null)
            dataSource.setMinimumIdle(properties.getMaxIdle());
        if (properties.getMaxWait() != null)
            dataSource.setConnectionTimeout(properties.getMaxWait());
//        if(properties.getTransactionIsolationName() != null) {
//            dataSource.setTransactionIsolation(properties.getTransactionIsolationName());
//        }
        return dataSource;
    }
}
