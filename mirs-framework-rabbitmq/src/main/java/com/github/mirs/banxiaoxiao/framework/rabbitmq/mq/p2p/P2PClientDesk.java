package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.RmqChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zcy 2018年10月12日
 */
public class P2PClientDesk {
    private Logger logger = LoggerFactory.getLogger(P2PClientDesk.class);
    private Thread heartThread;

    private Map<String, P2PClient> p2pClients = new HashMap<String, P2PClient>();

    public P2PClientDesk() {
        this.heartThread = new Thread() {

            public void run() {
                while (true) {
                    try {
                        sendHeart();
                    } catch (Throwable e) {
                        logger.error("send heart error", e);
                    }
                    try {
                        sleep(2000);
                    } catch (Throwable e) {
                        logger.error("send heart sleep error", e);
                    }
                }
            }
        };
        this.heartThread.setName("RMQ_P2P_HEARTBEAT_CLIENT");
        this.heartThread.setDaemon(true);
        this.heartThread.start();
    }

    public synchronized void registP2P(RmqChannel channel, String eventName) {
        if (!p2pClients.containsKey(eventName)) {
            P2PClient p2pClient = new P2PClient(channel, eventName);
            this.p2pClients.put(eventName, p2pClient);
        }
    }

    public synchronized void sendHeart() {
        for (P2PClient p2pClient : p2pClients.values()) {
            p2pClient.sendHeart();
        }
    }
}
