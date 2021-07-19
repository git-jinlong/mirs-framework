package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgPublisherFactory;

/**
 * @author zcy 2019年7月24日
 */
public interface P2PMsgPublisherFactory extends MsgPublisherFactory {

    public P2PMsgPublisher getP2PMsgPublisher(Class<?> clazz);
    
    public P2PMsgPublisher getP2PMsgPublisher(Class<?> clazz, P2PRouter router);
}
