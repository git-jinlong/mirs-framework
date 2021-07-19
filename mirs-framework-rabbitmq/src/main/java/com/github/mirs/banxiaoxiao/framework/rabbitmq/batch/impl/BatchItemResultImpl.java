package com.github.mirs.banxiaoxiao.framework.rabbitmq.batch.impl;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.batch.BatchItemResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Auther: lxj
 * @Date: 2019/8/15 16:30
 * @Description:
 */
public class BatchItemResultImpl<T> implements BatchItemResult<T> {

    private final Integer seqNum;
    private final Future<T> future;

    @Override
    public long getIndex() {
        return seqNum;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    public BatchItemResultImpl(Integer seqNum, Future<T> future) {
        this.seqNum = seqNum;
        this.future = future;
    }


    public Integer getSeqNum() {
        return seqNum;
    }

    public Future<T> getFuture() {
        return future;
    }
}
