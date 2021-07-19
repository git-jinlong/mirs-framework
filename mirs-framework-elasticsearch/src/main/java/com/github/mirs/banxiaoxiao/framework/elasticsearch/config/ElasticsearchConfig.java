package com.github.mirs.banxiaoxiao.framework.elasticsearch.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author: bc
 * @date: 2021-07-16 16:39
 **/
public class ElasticsearchConfig {

    @Autowired
    private ElasticsearchProperties elasticsearchProperties;



    @Bean(name = "highLevelClient")
    public RestClientBuilder restClientBuilder() {
        String[] ipAddress = elasticsearchProperties.getHostName().split(",");
        HttpHost[] hosts = Arrays.stream(ipAddress).map(this::makeHttpHost)
                .filter(Objects::nonNull)
                .toArray(HttpHost[]::new);
        RestClientBuilder restClientBuilder = RestClient.builder(hosts);
        if(StringUtils.isNotEmpty(elasticsearchProperties.getUserName()) && StringUtils.isNotEmpty(elasticsearchProperties.getPassword())){
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticsearchProperties.getUserName(), elasticsearchProperties.getPassword()));
            restClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                    return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
        }
        return restClientBuilder;
    }

    @Bean
    public RestHighLevelClient restHighLevelClient(@Autowired RestClientBuilder restClientBuilder){
        return new RestHighLevelClient(restClientBuilder);
    }

    private HttpHost makeHttpHost(String ipAddress) {
        return new HttpHost(ipAddress, elasticsearchProperties.getHttpPort());
    }

}
