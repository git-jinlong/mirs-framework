package com.github.mirs.banxiaoxiao.framework.dtask.annotation.enable;

import com.github.mirs.banxiaoxiao.framework.core.boot.EnableInitializer;

import java.lang.annotation.*;

/**
 * @author zcy 2019年5月29日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableInitializer(DTaskInitializer.class)
public @interface DTaskEnable {
}
