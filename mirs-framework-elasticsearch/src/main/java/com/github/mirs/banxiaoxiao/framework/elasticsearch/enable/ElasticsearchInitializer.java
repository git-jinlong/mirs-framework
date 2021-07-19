package com.github.mirs.banxiaoxiao.framework.elasticsearch.enable;

import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.boot.ModuleInitializer;
import com.github.mirs.banxiaoxiao.framework.elasticsearch.config.ElasticsearchConfig;
import com.github.mirs.banxiaoxiao.framework.elasticsearch.config.ElasticsearchProperties;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.annotation.Annotation;

/**
 * @author: bc
 * @date: 2021-07-16 16:37
 **/
public class ElasticsearchInitializer implements ModuleInitializer {
    @Override
    public void init(ConfigurableApplicationContext appContext) throws InitializeException {

        registerBean(ElasticsearchProperties.class, appContext);
        registerBean(ElasticsearchConfig.class, appContext);
        registerBean(ElasticsearchNonStaticUtil.class, appContext);
        registerBean(ElasticsearchUtil.class, appContext);
    }

    @Override
    public void init(Annotation enableAnno, ConfigurableApplicationContext appContext) throws InitializeException {

    }
}
