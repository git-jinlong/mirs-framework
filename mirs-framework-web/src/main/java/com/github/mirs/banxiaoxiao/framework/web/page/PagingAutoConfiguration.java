package com.github.mirs.banxiaoxiao.framework.web.page;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.Servlet;
import java.util.List;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto-configuration} enables
 * convenient paging in web mvc.
 *
 * @author zw
 */
@Configuration
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
@ConditionalOnWebApplication
@ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurerAdapter.class})
public class PagingAutoConfiguration {

    @Bean
    public PageParameterMethodArgumentResolver pageParameterMethodArgumentResolver() {
        return new PageParameterMethodArgumentResolver();
    }

    @Configuration
    public static class PagingWebMvcAutoConfiguration extends WebMvcConfigurerAdapter {

        private final PageParameterMethodArgumentResolver pageParameterMethodArgumentResolver;

        public PagingWebMvcAutoConfiguration(PageParameterMethodArgumentResolver pageParameterMethodArgumentResolver) {
            this.pageParameterMethodArgumentResolver = pageParameterMethodArgumentResolver;
        }

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(pageParameterMethodArgumentResolver);
        }
    }

}
