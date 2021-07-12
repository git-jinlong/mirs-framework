package com.github.mirs.banxiaoxiao.framework.core.monitor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.github.mirs.banxiaoxiao.framework.core.boot.EnableInitializer;
import com.github.mirs.banxiaoxiao.framework.core.monitor.MonitorApplicationInitializer;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AppMonitorConfig.class)
@EnableInitializer(MonitorApplicationInitializer.class)
public @interface MonitorEnable {
}
