package com.github.mirs.banxiaoxiao.framework.sftp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * @author: bc
 * @date: 2021-05-27 18:43
 **/
@Data
@ConfigurationProperties(prefix = "sftp.server")
public class FtpPathConfig implements Serializable {

    private String fileServerRootPath;
}
