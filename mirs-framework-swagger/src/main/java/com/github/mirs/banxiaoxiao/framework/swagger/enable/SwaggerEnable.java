package com.github.mirs.banxiaoxiao.framework.swagger.enable;

import com.github.mirs.banxiaoxiao.framework.core.boot.EnableInitializer;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.annotation.*;

/**
 * @author: bc
 * @date: 2021-03-03 14:09
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableInitializer(SwaggerInitializer.class)
@EnableSwagger2
@EnableKnife4j
public @interface SwaggerEnable {

}
