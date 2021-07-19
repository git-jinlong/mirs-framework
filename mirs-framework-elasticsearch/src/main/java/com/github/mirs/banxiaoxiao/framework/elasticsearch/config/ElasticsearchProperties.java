package com.github.mirs.banxiaoxiao.framework.elasticsearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: bc
 * @date: 2021-07-16 16:38
 **/
@ConfigurationProperties(prefix = "bee.elasticsearch")
public class ElasticsearchProperties {
    /**
     * 是否启用
     */
    private boolean enable;
    /**
     * elk集群地址
     */
    private String hostName;

    /**
     * 端口
     */
    @Deprecated
    private Integer port;
    /**
     * http端口
     */
    private Integer httpPort = 9200;

    /**
     * 集群名称
     */
    private String clusterName;

    /**
     * 连接池
     */
    @Deprecated
    private Integer poolSize;

    private String userName;

    private String password;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(Integer httpPort) {
        this.httpPort = httpPort;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
    }

    public boolean isCluster() {
        return hostName.contains(",");
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
