package com.github.mirs.banxiaoxiao.framework.rabbitmq.batch;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.RmqException;

/**
 * @Auther: lxj
 * @Date: 2019/8/15 18:35
 * @Description:
 */
public class TransactionRunTimeException extends RmqException {

    public TransactionRunTimeException() {
    }

    public TransactionRunTimeException(String msg) {
        super(msg);
    }

    public TransactionRunTimeException(Throwable e) {
        super(e);
    }

    public TransactionRunTimeException(String msg, Throwable e) {
        super(msg, e);
    }
}
