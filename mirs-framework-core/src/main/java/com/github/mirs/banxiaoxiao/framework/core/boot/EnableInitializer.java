package com.github.mirs.banxiaoxiao.framework.core.boot;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zcy 2019年4月15日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableInitializer {

    Class<? extends ApplicationInitializer>[] value();

    /**
     * 开启条件表达式，支持${}变量符号，${}变量会通过上下文及配置计算得到目标值，然后再执行脚本。 脚本语言默认采用JavaScript
     * 
     * @return
     */
    String condition() default "";
}
