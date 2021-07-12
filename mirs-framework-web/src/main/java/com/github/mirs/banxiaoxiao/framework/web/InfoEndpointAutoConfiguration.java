package com.github.mirs.banxiaoxiao.framework.web;//package com.arcvideo.cspm.web;
//
//import java.text.SimpleDateFormat;
//import java.util.Collections;
//import java.util.Date;
//import java.util.Map;
//import org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration;
//import org.springframework.boot.actuate.endpoint.InfoEndpoint;
//import org.springframework.boot.actuate.info.BuildInfoContributor;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
//import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
//import org.springframework.boot.info.BuildProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Auto-configuration class for override {@link InfoEndpoint}.
// *
// * @author zw
// */
//@Configuration
//@AutoConfigureBefore(EndpointAutoConfiguration.class)
//@AutoConfigureAfter(ProjectInfoAutoConfiguration.class)
//@ConditionalOnResource(resources = "${spring.info.build.location:classpath:META-INF/build-info.properties}")
//public class InfoEndpointAutoConfiguration {
//
//  @Bean
//  public InfoEndpoint infoEndpoint(BuildProperties properties) throws Exception {
//    return new InfoEndpoint(Collections.singletonList(new CustomBuildInfoContributor(properties)));
//  }
//
//
//  public static class CustomBuildInfoContributor extends BuildInfoContributor {
//
//
//    private CustomBuildInfoContributor(BuildProperties properties) {
//      super(properties);
//    }
//
//    @Override
//    protected void postProcessContent(Map<String, Object> content) {
//      super.postProcessContent(content);
//      Object value = content.get("time");
//      if (value != null) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
//        String str = format.format((Date) value);
//        content.put("time", str);
//      }
//    }
//  }
//}
