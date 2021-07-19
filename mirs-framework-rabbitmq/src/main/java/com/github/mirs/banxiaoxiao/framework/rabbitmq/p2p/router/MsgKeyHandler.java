package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.router;

/**
 * @author zcy 2019年7月23日
 */
public interface MsgKeyHandler {

    /**
     * 获取消息唯一主键
     * 
     * @return
     */
    public String getKey();
}
