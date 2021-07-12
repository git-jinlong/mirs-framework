package com.github.mirs.banxiaoxiao.framework.core.cross.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 跨网服务调用，CDS原单词：cross distributed services.服务发布和服务引用都使用CDS 作用在class上就为发布服务，作用在方法或者属性上就为引用服务
 * 
 * @author zcy 2020年2月26日
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
public @interface CDS {

    /**
     * 服务配置，采用kv模式 : k1=v1;k2=v2
     * 
     * @return
     */
    String config() default "";

    /**
     * 要发布的跨网服务接口，Void的情况下，将默认取实现类的第一个接口为发布的服务
     * 
     * @return
     */
    Class<?> interfaceClass() default void.class;

}
