package com.github.mirs.banxiaoxiao.framework.core.drpc.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mirs.banxiaoxiao.framework.core.boot.EnableInitializer;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableInitializer(value = DrpcInitializer.class)
@Import(DrpcEnableRegistrar.class)
public @interface DrpcEnable {
}
