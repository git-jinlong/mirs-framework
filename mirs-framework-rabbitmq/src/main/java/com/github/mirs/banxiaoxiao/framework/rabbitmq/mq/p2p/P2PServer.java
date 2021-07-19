package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.RmqChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zcy 2018年10月12日
 */
public class P2PServer {

    private Logger logger = LoggerFactory.getLogger(P2PServer.class);

    /** 单位ms，默认3秒，必须大于等于3s */
    private int expiry;

    private RmqChannel channel;

    private P2PClientListener clientListner;

    private Map<String, P2PClient> clients = new HashMap<String, P2PClient>();

    private String eventName;

    public P2PServer(RmqChannel channel, String eventName) {
        this(channel, eventName, 3000);
    }

    public P2PServer(RmqChannel channel, String eventName, int expiry) {
        this.channel = channel;
        this.eventName = eventName;
        if (expiry >= 3) {
            this.expiry = expiry;
        }
    }

    public void setP2PClientListener(P2PClientListener listener) {
        this.clientListner = listener;
    }

    public void onHeartbeat(Heartbeat heartbeat) {
        P2PClient client = getP2PClient(heartbeat);
        client.refreshHeattime();
    }

    private synchronized P2PClient getP2PClient(Heartbeat heartbeat) {
        logger.debug("client heartbeat [{},{},{}]", heartbeat.getClientId(), heartbeat.getEventName(), heartbeat.getClientAddress());
        P2PClient client = this.clients.get(heartbeat.getClientId());
        if (client == null) {
            client = new P2PClient(this.channel, this.eventName, heartbeat.getClientAddress());
            this.clients.put(heartbeat.getClientId(), client);
            noticeP2PClientListener();
        } else {
            client.setClientAddress(heartbeat.getClientAddress());
        }
        return client;
    }

    public synchronized void checkExpiry() {
        List<String> clientIds = new ArrayList<String>();
        for (String clientId : this.clients.keySet()) {
            P2PClient client = this.clients.get(clientId);
            if (client.isExpiry(this.expiry)) {
                clientIds.add(clientId);
            }
        }
        for (String clientId : clientIds) {
            logger.info("client {} expiry ", clientId);
            P2PClient client = this.clients.remove(clientId);
            try {
                client.expiry();
            } catch (Throwable e) {
                logger.error("client expiry fail.", e);
            }
        }
        if (clientIds.size() > 0) {
            noticeP2PClientListener();
        }
    }

    private void noticeP2PClientListener() {
        try {
            this.clientListner.refreshClient(new ArrayList<>(this.clients.values()));
        } catch (Throwable e) {
        }
    }
}
