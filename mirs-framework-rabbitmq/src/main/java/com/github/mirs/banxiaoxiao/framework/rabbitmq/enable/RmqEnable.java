package com.github.mirs.banxiaoxiao.framework.rabbitmq.enable;

import com.github.mirs.banxiaoxiao.framework.core.boot.EnableInitializer;

import java.lang.annotation.*;

/**
 * @author zcy 2019年6月19日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableInitializer(RmqInitializer.class)
public @interface RmqEnable {

    boolean p2pEnable() default false;
}
