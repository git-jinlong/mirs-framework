package com.github.mirs.banxiaoxiao.framework.rabbitmq.enable;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.EventBus;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgChannelAdmin;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgPublisherFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class EventBusConfig {

    @Autowired
    public void initBus(MsgPublisherFactory publisherFactory, MsgChannelAdmin rmqAdmin) {
        EventBus.init(publisherFactory, rmqAdmin);
    }
}
