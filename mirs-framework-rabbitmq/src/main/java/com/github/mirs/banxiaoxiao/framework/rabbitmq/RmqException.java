package com.github.mirs.banxiaoxiao.framework.rabbitmq;

/**
 * @author zcy 2019年6月24日
 */
public class RmqException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -6214952212126025228L;

    public RmqException() {
    }

    public RmqException(String msg) {
        super(msg);
    }

    public RmqException(Throwable e) {
        super(e);
    }

    public RmqException(String msg, Throwable e) {
        super(msg, e);
    }
}
