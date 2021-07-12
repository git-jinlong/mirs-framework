package com.github.mirs.banxiaoxiao.framework.sftp.pool;

import com.github.mirs.banxiaoxiao.framework.sftp.config.FtpClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.io.IOException;

/**
 * @desc:
 * @auther: bc
 * @date: 2021/05/24 09:25
 */
@Slf4j
public class FTPClientFactory extends BasePooledObjectFactory<FTPClient> {

    private FtpClientConfig ftpClientConfig;


    public FTPClientFactory(FtpClientConfig ftpClientConfig) {
        this.ftpClientConfig = ftpClientConfig;
    }


    /**
     * 新建对象
     */
    @Override
    public FTPClient create() throws Exception {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(ftpClientConfig.getClientTimeout());
        try {
            ftpClient.connect(ftpClientConfig.getHost(), ftpClientConfig.getPort());
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                log.error("FTPServer 拒绝连接");
                return null;
            }
            boolean result = ftpClient.login(ftpClientConfig.getUsername(), ftpClientConfig.getPassword());
            if (!result) {
                log.error("ftpClient登陆失败!");
                throw new Exception("ftpClient登陆失败! userName:" + ftpClientConfig.getUsername() + " ; password:" + ftpClientConfig.getPassword());
            }
            ftpClient.setFileType(ftpClientConfig.getTransferFileType());
            ftpClient.setBufferSize(ftpClientConfig.getBufferSize());
            ftpClient.setControlEncoding(ftpClientConfig.getEncoding());
            ftpClient.setDataTimeout(3000);
            if (ftpClientConfig.isPassiveMode()) {
                ftpClient.enterLocalPassiveMode();
            }
            if (StringUtils.isNotBlank(ftpClientConfig.getRootPath())) {
                ftpClient.changeWorkingDirectory(ftpClientConfig.getRootPath());
            }
        } catch (IOException e) {
            log.error("FTP连接失败：", e);
        }
        return ftpClient;
    }

    @Override
    public PooledObject<FTPClient> wrap(FTPClient ftpClient) {
        return new DefaultPooledObject<FTPClient>(ftpClient);
    }

    /**
     * 销毁对象
     */
    @Override
    public void destroyObject(PooledObject<FTPClient> p) throws Exception {
        FTPClient ftpClient = p.getObject();
        ftpClient.logout();
        super.destroyObject(p);
    }

    /**
     * 验证对象
     */
    @Override
    public boolean validateObject(PooledObject<FTPClient> p) {
        FTPClient ftpClient = p.getObject();
        boolean connect = false;
        try {
            connect = ftpClient.sendNoOp();
            if (connect) {
                ftpClient.changeWorkingDirectory(ftpClientConfig.getRootPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connect;
    }
}
