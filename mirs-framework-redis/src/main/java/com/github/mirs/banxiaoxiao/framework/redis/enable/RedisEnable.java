package com.github.mirs.banxiaoxiao.framework.redis.enable;

import com.github.mirs.banxiaoxiao.framework.core.boot.EnableInitializer;

import java.lang.annotation.*;

/**
 * @author bc
 * @version 1.0.0
 * @ClassName com.arcvideo.mirs.redis.enable.RedisEnable
 * @Description
 * @createTime 2021/3/26
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableInitializer(RedisInitializer.class)
public @interface RedisEnable {
    /**
     * 是否加载分布式锁
     *
     * @return
     */
    boolean useLock() default true;
}
