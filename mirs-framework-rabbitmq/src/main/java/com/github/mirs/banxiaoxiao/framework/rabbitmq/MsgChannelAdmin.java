package com.github.mirs.banxiaoxiao.framework.rabbitmq;

import java.util.List;
import java.util.Map;

/**
 * @author zcy 2019年5月22日
 */
public interface MsgChannelAdmin {

    /**
     * @param messageType
     * @param channelType
     * @return
     */
    public MsgChannel declareChannel(String msgClazzName, com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgChannelType type, Map<String, Object> configs);
    
    public MsgChannel declareChannel(Class<?> msgClazz, com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgChannelType type, Map<String, Object> configs);
    
    /**
     * @param priority
     * @param messageType
     * @param subscriber
     * @param method
     */
    public void bindSubscribe(int priority, MsgChannel channel, Object subscriber, String method);
    
    /**
     * @return
     */
    public List<MsgChannel> getMsgChannels();
    
    /**
     * @return
     */
    public List<MsgChannel> getSubscribeMsgChannels();
}
