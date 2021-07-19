package com.github.mirs.banxiaoxiao.framework.rabbitmq;





/**
 * @author zcy 2019年5月22日
 */
public interface MsgPublisherFactory {

    public MsgPublisher getMsgPublisher(com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgChannelType type, String msgClazzName);
    
    public MsgPublisher getMsgPublisher(com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgChannelType type, Class<?> msgClazz);
    
}
