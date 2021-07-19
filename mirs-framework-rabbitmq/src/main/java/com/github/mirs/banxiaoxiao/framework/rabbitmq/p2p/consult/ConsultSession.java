package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.consult;

import java.io.Serializable;

/**
 * p2p 多clinet协商会话
 * 
 * @author zcy 2019年6月27日
 */
public class ConsultSession<T> implements Serializable {

    /** */
    private static final long serialVersionUID = -2771753204965610310L;

    /** 会话事件类型 */
    private ConsultEventType eventType;

    private T body;

    public ConsultEventType getEventType() {
        return eventType;
    }

    public void setEventType(ConsultEventType eventType) {
        this.eventType = eventType;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
