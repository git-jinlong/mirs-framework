package com.github.mirs.banxiaoxiao.framework.core.drpc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
public @interface P2P {

    /**
     * 服务配置，采用kv模式 : k1=v1;k2=v2
     * 
     * @return
     */
    String config() default "";
    
    /**
     * @return
     */
    Class<?> interfaceClass() default void.class;
}
