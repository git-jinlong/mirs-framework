package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p;


/**
 * @author zcy 2019年7月19日
 */
public interface P2PRouter {

    /**
     * 选择发送给哪个client
     * @param obj 发送的消息
     * @return
     */
    public String choose(Object obj);

}
