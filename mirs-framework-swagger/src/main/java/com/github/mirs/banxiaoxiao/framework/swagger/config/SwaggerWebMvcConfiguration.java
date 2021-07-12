package com.github.mirs.banxiaoxiao.framework.swagger.config;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: bc
 * @date: 2021-05-07 17:02
 **/
public class SwaggerWebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        TComLogs.info("Add Resource Handlers");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
//
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
