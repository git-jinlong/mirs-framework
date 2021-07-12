package com.github.mirs.banxiaoxiao.framework.web.rest;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto-configuration} enables
 * {@link RestTemplate} and some convenient beans.
 *
 * @author zw
 */
@Configuration
@ConditionalOnClass({HttpClient.class, RestTemplate.class})
@EnableConfigurationProperties(RestConfigProperty.class)
public class RestTemplateAutoConfiguration {

  @Autowired
  private RestConfigProperty restConfigProperty;

  @Bean
  @ConditionalOnMissingBean(RestTemplate.class)
  public RestTemplate restTemplate() {
    PoolingHttpClientConnectionManager pcm = new PoolingHttpClientConnectionManager();
    pcm.setMaxTotal(restConfigProperty.getMaxConnection());

    final int timeoutSeconds = restConfigProperty.getMaxTimeoutSeconds();
    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(timeoutSeconds * 1000)
        .setSocketTimeout(timeoutSeconds * 1000)
        .setConnectionRequestTimeout(timeoutSeconds * 1000)
        .build();

    HttpClient client = HttpClients.custom()
        .setConnectionManager(pcm)
        .setDefaultRequestConfig(requestConfig)
        .build();
    ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);
    return new RestTemplate(factory);
  }

  @Bean
  @ConditionalOnMissingBean
  public RestTemplateHelper restTemplateHelper(RestTemplate restTemplate) {
    return new RestTemplateHelper(restTemplate);
  }


}
