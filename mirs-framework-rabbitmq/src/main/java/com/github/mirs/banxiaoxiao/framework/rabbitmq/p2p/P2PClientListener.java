package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.consult.P2PHeartbeat;

import java.util.List;

/**
 * @author zcy 2019年7月19日
 */
public interface P2PClientListener {

    public void onHeartbeat(List<P2PHeartbeat> clients);
}
