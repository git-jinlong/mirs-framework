package com.github.mirs.banxiaoxiao.framework.core.log.enable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mirs.banxiaoxiao.framework.core.boot.EnableInitializer;

/**
 * @author zcy 2020年1月21日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableInitializer(DynamicLogLevelInitializer.class)
public @interface LogEnable {
}
