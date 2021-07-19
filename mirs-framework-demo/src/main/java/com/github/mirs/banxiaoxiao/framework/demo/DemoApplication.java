package com.github.mirs.banxiaoxiao.framework.demo;

import com.github.mirs.banxiaoxiao.framework.core.boot.Application;
import com.github.mirs.banxiaoxiao.framework.core.boot.MirsStarter;
import com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.enable.IbatisEnable;
import com.github.mirs.banxiaoxiao.framework.redis.enable.RedisEnable;
import com.github.mirs.banxiaoxiao.framework.sftp.enable.SftpEnable;
import com.github.mirs.banxiaoxiao.framework.swagger.enable.SwaggerEnable;

/**
 * @author: bc
 * @date: 2021-07-13 11:05
 **/
@Application("arcmanager")
@IbatisEnable //开启mybatis以及mybatis-plus功能
@SwaggerEnable //开启swagger功能，内部使用knife4j增强版
@RedisEnable(useLock = true) //开启redis功能，useLock=true开启redis分布式锁的注入
@SftpEnable //开启vsftpd功能
public class DemoApplication extends MirsStarter {
    public static void main(String[] args) {
        run(DemoApplication.class, args);
    }
}
