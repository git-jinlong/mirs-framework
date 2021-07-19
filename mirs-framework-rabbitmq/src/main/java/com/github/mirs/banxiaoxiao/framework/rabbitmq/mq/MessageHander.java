package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq;


public interface MessageHander {
    
    public void handleMessage(Object event, int priority);
}
