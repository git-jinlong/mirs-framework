package com.github.mirs.banxiaoxiao.framework.rabbitmq.batch.impl;

import com.github.mirs.banxiaoxiao.framework.common.util.UUID;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.batch.BatchTransaction;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.batch.BatchTransactionManager;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rpc.RmqInvoker;

/**
 * @author zcy 2019年8月8日
 */
public class DefaultBatchTransactionManager<R> implements BatchTransactionManager {

    private RmqInvoker<R> rmqInvoker;

    @Override
    public BatchTransaction startBatchTransaction(Class<?> requestMsgType) {
        return new RPCBathTransactionImpl(rmqInvoker, UUID.random19());
    }

    public RmqInvoker<R> getRmqInvoker() {
        return rmqInvoker;
    }

    public void setRmqInvoker(RmqInvoker<R> rmqInvoker) {
        this.rmqInvoker = rmqInvoker;
    }
}
