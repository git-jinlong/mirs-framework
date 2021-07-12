package com.github.mirs.banxiaoxiao.framework.jdbc.jdbc.enable;

import com.github.mirs.banxiaoxiao.framework.core.boot.EnableInitializer;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableInitializer(JdbcInitializer.class)
public @interface JdbcEnable {
}
