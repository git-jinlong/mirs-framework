package com.github.mirs.banxiaoxiao.framework.core.woodpecker.enable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mirs.banxiaoxiao.framework.core.boot.EnableInitializer;
import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.annotation.DrpcEnable;

/**
 * @author zcy 2019年6月1日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DrpcEnable
@EnableInitializer({ WoodpeckerInitializer.class, WoodpeckerDrpcInitializer.class })
public @interface WoodpeckerEnable {
}
