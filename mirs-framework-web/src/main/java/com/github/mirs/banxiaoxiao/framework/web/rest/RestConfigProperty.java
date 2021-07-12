package com.github.mirs.banxiaoxiao.framework.web.rest;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Config properties for rest.
 *
 * @author zw
 */
@ConfigurationProperties(prefix = "rest")
public class RestConfigProperty {

  private int maxConnection = 100;

  private int maxTimeoutSeconds = 10;

  public int getMaxConnection() {
    return maxConnection;
  }

  public void setMaxConnection(int maxConnection) {
    this.maxConnection = maxConnection;
  }

  public int getMaxTimeoutSeconds() {
    return maxTimeoutSeconds;
  }

  public void setMaxTimeoutSeconds(int maxTimeoutSeconds) {
    this.maxTimeoutSeconds = maxTimeoutSeconds;
  }
}
