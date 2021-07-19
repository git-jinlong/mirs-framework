package com.github.mirs.banxiaoxiao.framework.elasticsearch.enable;

import com.github.mirs.banxiaoxiao.framework.core.boot.EnableInitializer;

import java.lang.annotation.*;

/**
 * @author: bc
 * @date: 2021-07-16 16:36
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableInitializer(ElasticsearchInitializer.class)
public @interface ElasticsearchEnable {


}
