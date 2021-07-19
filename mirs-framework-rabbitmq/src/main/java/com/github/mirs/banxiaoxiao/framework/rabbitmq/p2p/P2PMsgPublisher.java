package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgPublisher;

import java.util.Map;


/**
 * 
 * @author zcy 2019年6月25日
 */
public interface P2PMsgPublisher extends MsgPublisher {

    /**
     * 设置P2P发送地址
     * 
     * @param targetHost
     */
    public void setP2PRouter(P2PRouter router);
    
    public void send(Object msg, String routingKey, Map<String, Object> configs);
    
}
