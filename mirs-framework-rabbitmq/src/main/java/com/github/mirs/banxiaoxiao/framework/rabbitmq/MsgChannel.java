package com.github.mirs.banxiaoxiao.framework.rabbitmq;

import java.util.Map;

/**
 * 消息通道
 * 
 * @author zcy 2019年5月22日
 */
public interface MsgChannel {

    /**
     * 如果channel还没创建，则创建；如果已经存在，则不做任何处理
     */
    public void create();

    /**
     * 
     */
    public void clean();

    /**
     * 销毁channel，删除channel内还未消费的数据同时还会将channel的元数据删除
     */
    public void destory();

    /**
     * 获取消息通道消息数据类型
     * 
     * @return
     */
    public String getMsgType();

    /**
     * 通道类型
     * 
     * @return
     */
    public com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgChannelType getChannelType();

    /**
     * @return
     */
    public String getChannelId();

    /**
     * @param msg
     * @param configs
     */
    public void send(Object msg, String routingKey, Map<String, Object> configs);

    /**
     * @param msg
     * @param configs
     */
    public void send(Object msg, Map<String, Object> configs);

    /**
     * @return
     */
    public <T> T receive();
}
