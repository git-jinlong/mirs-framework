package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.annotation.Subscribe;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.RmqChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zcy 2018年10月12日
 */
public class P2PServerDesk {

    private Logger logger = LoggerFactory.getLogger(P2PServerDesk.class);

    private Map<String, P2PServer> p2pServers = new HashMap<String, P2PServer>();

    private Thread checkThread;

    private int expiry = 3000;

    public P2PServerDesk() {
        this.checkThread = new Thread() {

            public void run() {
                while (true) {
                    try {
                        sleep(6000);
                        startCheckExpiry();
                    } catch (Throwable e) {
                        logger.error("check expiry error", e);
                    }
                }
            }
        };
        this.checkThread.setName("RMQ_P2P_HEARTBEAT_SERVER");
        this.checkThread.setDaemon(true);
        this.checkThread.start();
    }

    private synchronized void startCheckExpiry() {
        for (P2PServer p2pServer : p2pServers.values()) {
            p2pServer.checkExpiry();
        }
    }

    @Subscribe({ Subscribe.Type.BROADCAST })
    public void onHeartbeat(Heartbeat heartbeat) {
        P2PServer p2pServer = p2pServers.get(heartbeat.getEventName());
        if (p2pServer != null) {
            p2pServer.onHeartbeat(heartbeat);
        } else {
            logger.debug("not exist p2p server {}", heartbeat.getEventName());
        }
    }

    public synchronized void registP2P(RmqChannel channel, String eventName, Object target) {
        if (!p2pServers.containsKey(eventName)) {
            if (target instanceof P2PClientListener) {
                P2PServer p2pServer = new P2PServer(channel, eventName, this.expiry);
                this.p2pServers.put(eventName, p2pServer);
                p2pServer.setP2PClientListener((P2PClientListener) target);
            } else {
                throw new IllegalArgumentException("the p2p target must implements com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p.P2PClientListener");
            }
        }
    }
}
