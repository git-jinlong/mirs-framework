package com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PMsgPublisher;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.P2PRouter;

import java.util.Map;

/**
 * @author zcy 2019年6月25日
 */
public class RabbitP2PMsgPublisher extends RabbitMsgPublisher implements P2PMsgPublisher {

    private P2PRouter router;

    public RabbitP2PMsgPublisher(P2PMsgChannel rabbitChannle, P2PRouter router) {
        super(rabbitChannle);
        this.router = router;
    }

    public RabbitP2PMsgPublisher(P2PMsgChannel rabbitChannle) {
        super(rabbitChannle);
    }

    public synchronized void publish(Object msg) {
        setTarget(msg);
        super.publish(msg);
    }

    public synchronized void publish(Object msg, int ttl) {
        setTarget(msg);
        super.publish(msg, ttl);
    }

    protected P2PMsgChannel getP2PMsgChannel() {
        return (P2PMsgChannel) rabbitChannle;
    }

    @Override
    public void setRabbitChannle(RabbitMsgChannel rabbitChannle) {
        super.setRabbitChannle(rabbitChannle);
    }

    @Override
    public void setP2PRouter(P2PRouter router) {
        this.router = router;
    }

    private void setTarget(Object msg) {
        if (router != null) {
            String target = router.choose(msg);
            getP2PMsgChannel().setTargetHost(target);
        }
    }

    @Override
    public void send(Object msg, String routingKey, Map<String, Object> configs) {
        rabbitChannle.send(msg, routingKey, configs);
        
    }
}
