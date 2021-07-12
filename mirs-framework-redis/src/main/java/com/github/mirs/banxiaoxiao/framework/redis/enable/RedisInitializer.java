package com.github.mirs.banxiaoxiao.framework.redis.enable;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.boot.ApplicationInitializer;
import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.redis.config.RedisAutoConfiguration;
import com.github.mirs.banxiaoxiao.framework.redis.config.lock.RedissonAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author bc
 * @version 1.0.0
 * @ClassName com.arcvideo.mirs.redis.enable.RedisInitializer
 * @Description
 * @createTime 2021/3/26
 */
public class RedisInitializer implements ApplicationInitializer {


    @Override
    public void init(ConfigurableApplicationContext applicationContext) throws InitializeException {
        registerBean(RedisAutoConfiguration.class, applicationContext);

        Class<?> applicationClass = BeeClientConfiguration.getLocalProperies().getApplicationClasss();
        RedisEnable redisEnable = doLoadRedisEnable(applicationClass);
        if (null == redisEnable) {
            return;
        }
        if (redisEnable.useLock()) {
            //使用分布式锁
            registerBean(RedissonAutoConfiguration.class, applicationContext);
        }
    }


    private RedisEnable doLoadRedisEnable(Class<?> clazz) {
        if (clazz == null || clazz.equals(Object.class) || StringUtil.startsWith(clazz.getName(), "java")) {
            return null;
        }

        RedisEnable redisEnable = AnnotationUtils.findAnnotation(clazz, RedisEnable.class);
        return redisEnable;
    }
}
