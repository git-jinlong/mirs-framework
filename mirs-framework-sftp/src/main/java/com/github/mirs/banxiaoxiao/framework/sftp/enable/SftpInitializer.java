package com.github.mirs.banxiaoxiao.framework.sftp.enable;

import com.github.mirs.banxiaoxiao.framework.core.boot.ApplicationInitializer;
import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.sftp.config.FtpClientConfig;
import com.github.mirs.banxiaoxiao.framework.sftp.config.FtpCustomConfiguration;
import com.github.mirs.banxiaoxiao.framework.sftp.config.FtpPathConfig;
import com.github.mirs.banxiaoxiao.framework.sftp.service.impl.SftpServiceImpl;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author: bc
 * @date: 2021-03-25 11:26
 **/
public class SftpInitializer implements ApplicationInitializer {
    @Override
    public void init(ConfigurableApplicationContext applicationContext) throws InitializeException {
        //
        registerBean(FtpPathConfig.class, applicationContext);
        registerBean(FtpCustomConfiguration.class, applicationContext);
        registerBean(FtpClientConfig.class, applicationContext);
        //注册sftp service
        registerBean(SftpServiceImpl.class, applicationContext);
    }
}
