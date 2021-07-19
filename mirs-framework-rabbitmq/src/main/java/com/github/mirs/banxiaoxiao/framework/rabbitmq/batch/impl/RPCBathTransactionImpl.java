package com.github.mirs.banxiaoxiao.framework.rabbitmq.batch.impl;

import com.github.mirs.banxiaoxiao.framework.common.util.JsonUtils;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.EventErrorCode;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.batch.*;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rpc.RmqInvoker;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Auther: lxj
 * @Date: 2019/8/13 16:53
 * @Description:
 */
public class RPCBathTransactionImpl<R> implements BatchTransaction {

    private volatile int state = 0;
    private static final int PUBLISHING = 1;
    private static final int COMMITED = 2;
    private static final int END = 3;

    private final RmqInvoker<R> rmqInvoker;//mq分发机制
    private final ReentrantLock lock = new ReentrantLock();
    private final String sessionId; //事务id
    private final AtomicInteger index = new AtomicInteger(0);//发送指令序列
    private final Map<Integer, R> indexObj = Maps.newConcurrentMap();//保存消息发送序列

    public RPCBathTransactionImpl(RmqInvoker<R> rmqInvoker, String sessionId) {
        this.rmqInvoker = rmqInvoker;
        this.sessionId = sessionId;
    }

    @Override
    public String getTxid() {
        return sessionId;
    }

    @Override
    public int publish(Object event) throws TransactionOverException {
        lock.lock();
        try {
            if (event == null) {
                return EventErrorCode.INVALID_MESSAGE;
            }
            if (state > PUBLISHING) throw new TransactionOverException("The transaction has been committed !");
            int seqNum = index.incrementAndGet();
            indexObj.putIfAbsent(seqNum, (R) event);
            if (state != PUBLISHING) state = PUBLISHING;
            return seqNum;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <V> BatchResult<V> commit() throws TransactionOverException {
        lock.lock();
        try {
            if (index.get() == 0) throw new InterruptedException("No tasks were published,you have to submit one!");
            if (state > PUBLISHING) throw new TransactionOverException("The transaction has been committed !");
            if (state != COMMITED) state = COMMITED;

            List<BatchItemResult<V>> batchItemResults = new ArrayList<>();
            for (Map.Entry<Integer, ?> entry : indexObj.entrySet()) {
                Future<V> future = rmqInvoker.invokeAsyn((R) entry.getValue());
                BatchItemResult<V> batchItemResult = new BatchItemResultImpl(entry.getKey(), future);
                batchItemResults.add(batchItemResult);
            }
            return new BatchResultImpl(sessionId, batchItemResults);
        } catch (InterruptedException e) {
            TComLogs.info("deal request InterruptedException ,data info :{}", JsonUtils.toJson(indexObj));
            throw new TransactionRunTimeException(e);
        } finally {
            if (state != END) state = END;
            lock.unlock();
        }
    }
}
