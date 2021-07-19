package com.github.mirs.banxiaoxiao.framework.rabbitmq;

/**
 * @author zcy 2019年5月22日
 */
public interface MsgPublisher {

    /**
     * @param msh
     * @return
     */
    public void publish(Object msg);

    /**
     * @param msh
     * @param ttl
     * @return
     */ 
    public void publish(Object msg, int ttl);
    
}
