package com.github.mirs.banxiaoxiao.framework.jdbc.jdbc;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("bee.datasource.slave")
public class SlaveDataSourceProperties extends DataSourceProperties {
}
