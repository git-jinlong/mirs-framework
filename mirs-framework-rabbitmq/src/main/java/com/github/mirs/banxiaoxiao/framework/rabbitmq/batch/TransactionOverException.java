package com.github.mirs.banxiaoxiao.framework.rabbitmq.batch;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.RmqException;

/**
 * @author zcy 2019年8月13日
 */
public class TransactionOverException extends RmqException {

    /**
     *
     */
    private static final long serialVersionUID = 1300936756949282975L;

    public TransactionOverException() {
    }

    public TransactionOverException(String msg) {
        super(msg);
    }

    public TransactionOverException(Throwable e) {
        super(e);
    }

    public TransactionOverException(String msg, Throwable e) {
        super(msg, e);
    }
}
