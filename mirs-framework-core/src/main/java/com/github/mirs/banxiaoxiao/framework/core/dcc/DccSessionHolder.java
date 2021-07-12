package com.github.mirs.banxiaoxiao.framework.core.dcc;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author zcy 2018年10月9日
 */
public class DccSessionHolder implements Watcher {

    private Logger logger = LoggerFactory.getLogger(DccSessionHolder.class);

    private Object waiter = new Object();

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private String zkHostPort;

    private int connectTimeout;

    private ZooKeeper zk;

    private List<HoldListener> linsteners;

    private String username;

    private String password;

    public DccSessionHolder(String zkHostPort, int connectTimeout) {
        this.zkHostPort = zkHostPort;
        this.connectTimeout = connectTimeout;
        this.linsteners = new ArrayList<HoldListener>();
    }

    public DccSessionHolder(String zkHostPort, int connectTimeout,String username,String password) {
        this.zkHostPort = zkHostPort;
        this.connectTimeout = connectTimeout;
        this.linsteners = new ArrayList<HoldListener>();
        this.username = username;
        this.password = password;
    }

    public void registListener(HoldListener holdListener) {
        this.linsteners.add(holdListener);
    }

    private void notifyReconnectListener(ZooKeeper zk) {
        for (HoldListener listener : this.linsteners) {
            try {
                listener.onReconnect(zk);
            } catch (Exception e) {
                logger.error("notify holdListener error", e);
            }
        }
    }
    
    private void notifyConnectedListener(ZooKeeper zk) {
        for (HoldListener listener : this.linsteners) {
            try {
                listener.onConnected(zk);
            } catch (Exception e) {
                logger.error("notify holdListener error", e);
            }
        }
    }

    /**
     * <p>
     * 连接Zookeeper
     * </p>
     * 启动zk服务 本实例基于自动重连策略,如果zk连接没有建立成功或者在运行时断开,将会自动重连.
     * 
//     * @param connectString
//     * @param sessionTimeout
     */
    public synchronized ZooKeeper connect() {
        try {
            if (zk == null) {
                zk = new ZooKeeper(zkHostPort, connectTimeout, this);
                /** 认证类型 */
                if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)){
                    String scheme = "digest";
                    String pass = username.trim() + ":" + password.trim();
                    zk.addAuthInfo(scheme,pass.getBytes());
                    //定义一个用户名密码
                    Id id = null;
                    try {
                        id = new Id(scheme, DigestAuthenticationProvider.generateDigest(pass));
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    ACL acl = new ACL(ZooDefs.Perms.ALL, id);
                    List<ACL> acls = new ArrayList<>();
                    acls.add(acl);
                    //如果修改已经加密的节点，请先按原用户密码认证登录
                    try {
                        zk.setACL("/", acls, -1);
                        zk.setACL("/dubbo", acls, -1);
                        zk.setACL("/zookeeper", acls, -1);
                        zk.setACL("/bee", acls, -1);
                        zk.setACL("/arc_iface", acls, -1);
                        TComLogs.info("ZooKeeper setACL succss!!!");
                    } catch (KeeperException e) {
                        e.printStackTrace();
                        TComLogs.error("ZooKeeper connect KeeperException {},username {}|password {},",e.getMessage(),username ,password );
                    };
                }
                countDownLatch.await();
            }
        } catch (IOException e) {
            throw new DccConnectException("连接创建失败，发生 IOException ", e);
        } catch (InterruptedException e) {
            throw new DccConnectException("连接创建失败，发生 InterruptedException ", e);
        }
        return this.zk;
    }

    public void close() {
        try {
            synchronized (waiter) {
                if (zk != null) {
                    zk.close();
                }
                waiter.notifyAll();
            }
            this.zk = null;
        } catch (Throwable e) {
            logger.error("release connection error ," + e.getMessage(), e);
        }
    }

    public void process(WatchedEvent event) {
        // 如果是“数据变更”事件
        if (event.getType() != Event.EventType.None) {
            return;
        }
        synchronized (waiter) {
            switch (event.getState()) {
            case SyncConnected:
                // zk连接建立成功,或者重连成功
                countDownLatch.countDown();
                waiter.notifyAll();
                logger.info("Connected...");
                notifyConnectedListener(this.zk);
                break;
            case Expired:
                // session过期,这是个非常严重的问题,有可能client端出现了问题,也有可能zk环境故障
                // 此处仅仅是重新实例化zk client
                logger.error("Expired(尝试一次重连)...");
                close();
                ZooKeeper zk = connect();
                notifyReconnectListener(zk);
                break;
            case Disconnected:
                logger.error("Disconnected(尝试一次重连)....");
                close();
                ZooKeeper zk2 = connect();
                notifyReconnectListener(zk2);
                break;
            case AuthFailed:
                close();
                throw new RuntimeException("ZK Connection auth failed...");
            default:
                break;
            }
        }
    }

    static interface HoldListener {

        void onReconnect(ZooKeeper zk);
        
        void onConnected(ZooKeeper zk);
    }
}