package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p;

import java.util.List;

/**
 * @see com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PClientListener
 * @see com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PMsgPublisher
 * @author zcy 2018年10月12日
 */
@Deprecated
public interface P2PClientListener {

    /**
     * 能接收到目标P2P事件的客户端列表
     * @param clients
     */
    void refreshClient(List<P2PClient> clients);
}
