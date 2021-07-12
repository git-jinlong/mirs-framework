package com.github.mirs.banxiaoxiao.framework.core.drpc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * service api (Internal platform)
 * 
 * @author erxiao 2017年1月21日
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
public @interface DS {

    /**
     * 服务配置，采用kv模式 : k1=v1;k2=v2
     * @return
     */
    String config() default "";
    
    /**
     * @return
     */
    Class<?> interfaceClass() default void.class;
    
    /**
     * 所属group
     * @return
     */
    String group() default "";
    
    /**
     * @return
     */
    Class<?> groupLoader() default void.class;
}
