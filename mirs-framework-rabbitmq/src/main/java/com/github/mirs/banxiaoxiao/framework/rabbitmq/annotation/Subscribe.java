package com.github.mirs.banxiaoxiao.framework.rabbitmq.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author erxiao 2017年2月8日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {

    Type[] value() default Type.QUEUE;

    enum Type {
        /** 广播模式 */
        BROADCAST,
        /** 队列模式 */
        QUEUE,
        /** P2P模式 */
        P2P,
    }

    String queueName() default "";

    int queueMaxLength() default 0;

    int queueCategory() default 0;

    int messageTtl() default 0;
    
    int prefetchCount() default 0;
    
    boolean durable() default true;
}
