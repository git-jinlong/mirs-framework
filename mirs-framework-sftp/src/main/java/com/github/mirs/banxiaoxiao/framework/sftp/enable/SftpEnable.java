package com.github.mirs.banxiaoxiao.framework.sftp.enable;

import com.github.mirs.banxiaoxiao.framework.core.boot.EnableInitializer;

import java.lang.annotation.*;

/**
 * @author: bc
 * @date: 2021-03-25 11:26
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableInitializer(SftpInitializer.class)
public @interface SftpEnable {
}
