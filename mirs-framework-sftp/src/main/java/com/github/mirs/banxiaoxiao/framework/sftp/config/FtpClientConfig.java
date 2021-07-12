package com.github.mirs.banxiaoxiao.framework.sftp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

/**
 * @Auther: bc
 * @Date: 2021/05/24 09:19
 * @Description: 配置信息类
 */
@ConfigurationProperties(prefix = "sftp.client")
public class FtpClientConfig {

    //ftp应用类型
    private String ftpType;
    //地址
    private String host;
    //端口
    private int port;
    //账户名
    private String username;
    //密码
    private String password;
    //上传路径
    public String rootPath;

    //被动模式
    private boolean passiveMode;
    //阻塞时间
    private Long syncOutTime_MINUTES;
    //编码
    private String encoding = "UTF-8";
    //超时时间
    private int clientTimeout = 6000;
    //线程数
    private int threadNum = 1;
    //文件传送类型 0=ASCII_FILE_TYPE（ASCII格式） 1=EBCDIC_FILE_TYPE 2=LOCAL_FILE_TYPE（二进制文件）
    private int transferFileType = 2;
    //是否重命名
    private boolean renameUploaded = false;
    //重新连接时间
    private int retryTimes = 1200;
    //缓存大小
    private int bufferSize = 1024;
    //主机ip信息
    private String key;


    //连接池配置

    //最大数
    private int pool_maxTotal = 10;
    //最小空闲
    private int pool_minIdle = 5;
    //最大空闲
    private int pool_maxIdle = 10;
    //最大等待时间
    private int pool_maxWait = 3000;
    //池对象耗尽之后是否阻塞,maxWait<0时一直等待
    private boolean pool_blockWhenExhausted = true;
    //取对象是验证
    private boolean pool_testOnBorrow = true;
    //回收验证
    private boolean pool_testOnReturn = true;
    //创建时验证
    private boolean pool_testOnCreate = true;
    //空闲验证
    private boolean pool_testWhileIdle = false;
    //后进先出
    private boolean pool_lifo = false;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFtpType() {
        return ftpType;
    }

    public void setFtpType(String ftpType) {
        this.ftpType = ftpType;
    }

    public boolean isPassiveMode() {
        return passiveMode;
    }

    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }

    public Long getSyncOutTime_MINUTES() {
        if (Objects.isNull(syncOutTime_MINUTES)) {
            return 1l;
        }
        return syncOutTime_MINUTES;
    }

    public void setSyncOutTime_MINUTES(Long syncOutTime_MINUTES) {
        this.syncOutTime_MINUTES = syncOutTime_MINUTES;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int getClientTimeout() {
        return clientTimeout;
    }

    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public int getTransferFileType() {
        return transferFileType;
    }

    public void setTransferFileType(int transferFileType) {
        this.transferFileType = transferFileType;
    }

    public boolean isRenameUploaded() {
        return renameUploaded;
    }

    public void setRenameUploaded(boolean renameUploaded) {
        this.renameUploaded = renameUploaded;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getPool_maxTotal() {
        return pool_maxTotal;
    }

    public void setPool_maxTotal(int pool_maxTotal) {
        this.pool_maxTotal = pool_maxTotal;
    }

    public int getPool_minIdle() {
        return pool_minIdle;
    }

    public void setPool_minIdle(int pool_minIdle) {
        this.pool_minIdle = pool_minIdle;
    }

    public int getPool_maxIdle() {
        return pool_maxIdle;
    }

    public void setPool_maxIdle(int pool_maxIdle) {
        this.pool_maxIdle = pool_maxIdle;
    }

    public int getPool_maxWait() {
        return pool_maxWait;
    }

    public void setPool_maxWait(int pool_maxWait) {
        this.pool_maxWait = pool_maxWait;
    }

    public boolean isPool_blockWhenExhausted() {
        return pool_blockWhenExhausted;
    }

    public void setPool_blockWhenExhausted(boolean pool_blockWhenExhausted) {
        this.pool_blockWhenExhausted = pool_blockWhenExhausted;
    }

    public boolean isPool_testOnBorrow() {
        return pool_testOnBorrow;
    }

    public void setPool_testOnBorrow(boolean pool_testOnBorrow) {
        this.pool_testOnBorrow = pool_testOnBorrow;
    }

    public boolean isPool_testOnReturn() {
        return pool_testOnReturn;
    }

    public void setPool_testOnReturn(boolean pool_testOnReturn) {
        this.pool_testOnReturn = pool_testOnReturn;
    }

    public boolean isPool_testOnCreate() {
        return pool_testOnCreate;
    }

    public void setPool_testOnCreate(boolean pool_testOnCreate) {
        this.pool_testOnCreate = pool_testOnCreate;
    }

    public boolean isPool_testWhileIdle() {
        return pool_testWhileIdle;
    }

    public void setPool_testWhileIdle(boolean pool_testWhileIdle) {
        this.pool_testWhileIdle = pool_testWhileIdle;
    }

    public boolean isPool_lifo() {
        return pool_lifo;
    }

    public void setPool_lifo(boolean pool_lifo) {
        this.pool_lifo = pool_lifo;
    }
}
