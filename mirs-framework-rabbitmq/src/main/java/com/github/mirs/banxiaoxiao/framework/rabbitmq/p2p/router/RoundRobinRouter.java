package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.router;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.AbstractMsgClientListener;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PRouter;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.consult.ConsultationDeskHelper;

import java.util.List;

/**
 * @author zcy 2019年7月19日
 */
public class RoundRobinRouter extends AbstractMsgClientListener implements P2PRouter {

    private int index = 0;

    public RoundRobinRouter(Class<?> msgType) {
        super(msgType);
        ConsultationDeskHelper.addClientListener(this);
    }

    @Override
    public synchronized String choose(Object msg) {
        List<String> clientHosts = getClients();
        if (clientHosts == null || clientHosts.size() == 0) {
            return null;
        } else {
            if (index >= clientHosts.size()) {
                index = 0;
            }
            String clientId = clientHosts.get(index);
            index ++;
            return clientId;
        }
    }

}
