package com.github.mirs.banxiaoxiao.framework.sftp.pool;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.sftp.config.FtpClientConfig;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @desc:
 * @auther: bc
 * @date: 2021/05/24 09:42
 */
public class FTPClientPool {

    private GenericObjectPool<FTPClient> ftpClientPool;
    /**
     * 全局阻塞，超过次数
     */
    private int blockedTimeNum = 3;
    /**
     * 全局阻塞次数
     */
    private int bolckedTimeNum = 0;

    private FtpClientConfig ftpClientConfig;

    private GenericObjectPoolConfig genericObjectPoolConfig;

    public FTPClientPool(FtpClientConfig ftpClientConfig) {

        // 初始化对象池配置
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setBlockWhenExhausted(ftpClientConfig.isPool_blockWhenExhausted());
        poolConfig.setMaxWaitMillis(Long.valueOf(ftpClientConfig.getPool_maxWait()));
        poolConfig.setMinIdle(Integer.valueOf(ftpClientConfig.getPool_maxIdle()));
        poolConfig.setMaxIdle(Integer.valueOf(ftpClientConfig.getPool_maxWait()));
        poolConfig.setMaxTotal(Integer.valueOf(ftpClientConfig.getPool_maxTotal()));
        poolConfig.setTestOnBorrow(ftpClientConfig.isPool_testOnBorrow());
        poolConfig.setTestOnReturn(ftpClientConfig.isPool_testOnReturn());
        poolConfig.setTestOnCreate(ftpClientConfig.isPool_testOnCreate());
        poolConfig.setTestWhileIdle(ftpClientConfig.isPool_testWhileIdle());
        poolConfig.setLifo(ftpClientConfig.isPool_lifo());
        this.ftpClientConfig = ftpClientConfig;
        this.genericObjectPoolConfig = poolConfig;

        // 初始化对象池
        ftpClientPool = new GenericObjectPool<>(new FTPClientFactory(ftpClientConfig), poolConfig);
    }

    public FTPClient borrowObject() {
        try {
            TComLogs.debug("获取前");
            TComLogs.debug("活动" + ftpClientPool.getNumActive());
            TComLogs.debug("等待" + ftpClientPool.getNumWaiters());
            TComLogs.debug("----------");
            return ftpClientPool.borrowObject();
        } catch (Exception e) {
            TComLogs.error("争抢FTP失败,{}", e);
            if (getActiveNum() == getMaxNum()) {
                if (bolckedTimeNum >= getBlockedTimeNum()) {
                    //全部销毁
                    clear();
                    //阻塞时间设置成0
                    bolckedTimeNum = 0;
                } else {
                    bolckedTimeNum = bolckedTimeNum + 1;
                }
            }
        }
        return null;
    }

    public void returnObject(FTPClient ftpClient) {

        /*System.out.println("归还前");
        System.out.println("活动"+ftpClientPool.getNumActive());
        System.out.println("等待"+ftpClientPool.getNumWaiters());
        System.out.println("----------");*/
        ftpClientPool.returnObject(ftpClient);
        TComLogs.debug("归还后");
        TComLogs.debug("活动" + ftpClientPool.getNumActive());
        TComLogs.debug("等待" + ftpClientPool.getNumWaiters());
        TComLogs.debug("----------");
    }

    public int getActiveNum() {
        return ftpClientPool.getNumActive();
    }

    public int getMaxNum() {
        return ftpClientPool.getMaxTotal();
    }

    public int getBlockedTimeNum() {
        return blockedTimeNum;
    }

    public void clear() {
        try {
            ftpClientPool.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ftpClientPool = new GenericObjectPool<>(new FTPClientFactory(ftpClientConfig), genericObjectPoolConfig);
    }
}
