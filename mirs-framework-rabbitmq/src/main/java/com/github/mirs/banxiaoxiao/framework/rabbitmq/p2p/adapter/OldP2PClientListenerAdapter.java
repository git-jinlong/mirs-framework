package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.adapter;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p.P2PClient;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.AbstractMsgClientListener;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PMsgPublisher;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PMsgPublisherFactory;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.consult.P2PHeartbeat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zcy 2019年7月19日
 */
public class OldP2PClientListenerAdapter extends AbstractMsgClientListener {

    private com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p.P2PClientListener oldListener;

    private P2PMsgPublisherFactory factory;

    public OldP2PClientListenerAdapter(com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p.P2PClientListener oldListener, P2PMsgPublisherFactory factory, Class<?> msgType) {
        super(msgType);
        this.oldListener = oldListener;
        this.factory = factory;
    }

    @Override
    public void onHeartbeat(List<P2PHeartbeat> clients) {
        super.onHeartbeat(clients);
        List<String> supportClient = super.getClients();
        List<P2PClient> oldClients = new ArrayList<P2PClient>();
        if (supportClient != null) {
            for (String host : supportClient) {
                P2PMsgPublisher publisher = factory.getP2PMsgPublisher(getMsgType());
                P2PClient p2pClient = new OldP2PClient(null, getMsgType().getName(), publisher, host);
                oldClients.add(p2pClient);
            }
        }
        oldListener.refreshClient(oldClients);
    }

}
