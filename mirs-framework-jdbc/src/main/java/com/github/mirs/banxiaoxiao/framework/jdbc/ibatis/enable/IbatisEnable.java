package com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.enable;

import com.github.mirs.banxiaoxiao.framework.core.boot.EnableInitializer;
import com.github.mirs.banxiaoxiao.framework.jdbc.jdbc.enable.JdbcEnable;

import java.lang.annotation.*;

/**
 * @author zcy 2019年5月21日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableInitializer(IbatisInitializer.class)
@JdbcEnable
public @interface IbatisEnable {

    /**
     * 是否自动注入Mapper注解的dao
     *
     * @return
     */
    boolean autoMapper() default true;

    boolean autoIntercepts() default true;
}
