package com.github.mirs.banxiaoxiao.framework.sftp.config;

import com.github.mirs.banxiaoxiao.framework.sftp.helper.FtpHelper;

/**
 * @author: bc
 * @date: 2021-05-27 20:43
 **/
public class FtpCustomConfiguration {
    private final FtpPathConfig ftpPathConfig;

    public FtpCustomConfiguration(FtpPathConfig ftpPathConfig) {
        this.ftpPathConfig = ftpPathConfig;
        FtpHelper.getInstance().setFileServer(this.ftpPathConfig.getFileServerRootPath());
    }

}
