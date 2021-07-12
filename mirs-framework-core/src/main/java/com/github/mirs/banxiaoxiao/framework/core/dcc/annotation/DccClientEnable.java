package com.github.mirs.banxiaoxiao.framework.core.dcc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mirs.banxiaoxiao.framework.core.boot.EnableInitializer;
import com.github.mirs.banxiaoxiao.framework.core.dcc.DccApplicationInitializer;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableInitializer(DccApplicationInitializer.class)
public @interface DccClientEnable {
}
