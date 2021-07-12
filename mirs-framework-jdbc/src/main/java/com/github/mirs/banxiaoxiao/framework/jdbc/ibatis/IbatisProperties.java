/**
 *
 */
package com.github.mirs.banxiaoxiao.framework.jdbc.ibatis;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import org.apache.ibatis.session.ExecutorType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Properties;

/**
 * @author erxiao 2017年2月14日
 */
@ConfigurationProperties("bee.mybatis")
public class IbatisProperties {

    private String configLocation = "classpath*:mybatis.xml";

    private String mapperLocations = "classpath*:mapper/**/*.xml";

    private String typeAliasesPackage;

    private String typeEnumsPackage;

    private ExecutorType executorType;
    // 数据库的固定前缀，如果不设置可忽略，默认的key为prefix，可用${prefix}
    private String dataSourcePrefix;
    //数据库类型
    private String dataBaseType = "mariadb";


    /**
     * Externalized properties for MyBatis configuration.
     */
    private Properties configurationProperties;


    /**
     * TODO 全局配置
     */
    @NestedConfigurationProperty
    private GlobalConfig globalConfig ;


    public Properties getConfigurationProperties() {
        return configurationProperties;
    }

    public void setConfigurationProperties(Properties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public String getDataBaseType() {
        return dataBaseType;
    }

    public void setDataBaseType(String dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    public String getDataSourcePrefix() {
        return dataSourcePrefix;
    }

    public void setDataSourcePrefix(String dataSourcePrefix) {
        this.dataSourcePrefix = dataSourcePrefix;
    }

    public String getTypeEnumsPackage() {
        return typeEnumsPackage;
    }

    public void setTypeEnumsPackage(String typeEnumsPackage) {
        this.typeEnumsPackage = typeEnumsPackage;
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public String getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public String getTypeAliasesPackage() {
        return typeAliasesPackage;
    }

    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    public ExecutorType getExecutorType() {
        return executorType;
    }

    public void setExecutorType(ExecutorType executorType) {
        this.executorType = executorType;
    }

}
