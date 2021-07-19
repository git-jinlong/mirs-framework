package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.adapter;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.EventErrorCode;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.RmqChannel;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.mq.p2p.P2PClient;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PMsgPublisher;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PRouter;

/**
 * @author zcy 2019年7月19日
 */
public class OldP2PClient extends P2PClient {

    private P2PMsgPublisher publisher;

    private P2PRouter router;
    
    private String eventName;

    public OldP2PClient(RmqChannel connection, String eventName, P2PMsgPublisher publisher, String host) {
        super(connection, eventName, host);
        this.publisher = publisher;
        this.eventName = eventName;
        this.router = new SingleClientRouter(host);
    }

    public int publish(Object event) {
        // 临时修复下
        String routeKey = this.eventName + "_" + router.choose(event);
        publisher.send(event, routeKey, null);
        return EventErrorCode.SUCCEED;
    }

    static class SingleClientRouter implements P2PRouter {

        private String targetClient;

        public SingleClientRouter(String targetClient) {
            super();
            this.targetClient = targetClient;
        }

        @Override
        public String choose(Object obj) {
            return targetClient;
        }
    }
}
