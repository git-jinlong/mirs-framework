package com.github.mirs.banxiaoxiao.framework.core.cross.annotation;

import com.github.mirs.banxiaoxiao.framework.core.boot.EnableInitializer;
import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.annotation.DrpcEnable;

import java.lang.annotation.*;

/**
 * @Auther: lxj
 * @Date: 2020/3/23 15:17
 * @Description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DrpcEnable
@EnableInitializer(CDSInitializer.class)
public @interface CrossEnable {

}
