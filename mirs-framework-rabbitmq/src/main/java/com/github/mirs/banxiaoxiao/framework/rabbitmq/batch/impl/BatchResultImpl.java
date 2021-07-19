package com.github.mirs.banxiaoxiao.framework.rabbitmq.batch.impl;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.batch.BatchItemResult;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.batch.BatchResult;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.batch.TransactionRunTimeException;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Auther: lxj
 * @Date: 2019/8/14 10:17
 * @Description:
 */
public class BatchResultImpl<V> implements BatchResult<V> {

    private final String sessionId;
    private final List<BatchItemResult<V>> batchItemResults;

    public BatchResultImpl(String sessionId, List<BatchItemResult<V>> batchItemResults) {
        this.sessionId = sessionId;
        this.batchItemResults = batchItemResults;
    }

    @Override
    public String getTxid() {
        return sessionId;
    }

    @Override
    public List<BatchItemResult<V>> getItemResults() {
        return batchItemResults;
    }

    @Override
    public BatchItemResult<V> getItemResult(int index) {
        if (CollectionUtils.isEmpty(batchItemResults)) return null;
        for (BatchItemResult<V> batchItemResult : batchItemResults) {
            if (batchItemResult.getIndex() == index) {
                return batchItemResult;
            }
        }
        return null;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (CollectionUtils.isEmpty(batchItemResults)) return true;
        for (BatchItemResult<V> batchItemResult : batchItemResults) {
            if (!batchItemResult.cancel(mayInterruptIfRunning)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isCancelled() {
        if (CollectionUtils.isEmpty(batchItemResults)) return true;
        for (BatchItemResult<V> batchItemResult : batchItemResults) {
            if (!batchItemResult.isCancelled()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isDone() {
        if (CollectionUtils.isEmpty(batchItemResults)) return true;
        for (BatchItemResult<V> batchItemResult : batchItemResults) {
            if (!batchItemResult.isDone()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<V> get() throws InterruptedException, ExecutionException {
        if (CollectionUtils.isEmpty(batchItemResults)) return Lists.newArrayList();
        List<V> datas = new ArrayList<>();
        for (BatchItemResult<V> batchItemResult : batchItemResults) {
            datas.add(batchItemResult.get());
        }
        return datas;
    }

    @Override
    public List<V> get(long timeout, TimeUnit unit) throws InterruptedException {
        if (CollectionUtils.isEmpty(batchItemResults)) return Lists.newArrayList();
        List<V> datas = new ArrayList<>();
        CountDownLatch count = new CountDownLatch(1);
        TaskExecutorsHandle.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    for (BatchItemResult<V> batchItemResult : batchItemResults) {
                        datas.add(batchItemResult.get(timeout, unit));
                    }
                } catch (InterruptedException e) {
                    TComLogs.info(" BatchResultImpl.get(long timeout, TimeUnit unit) happened InterruptedException , msg : {}", e.getMessage());
                    throw new TransactionRunTimeException(e);
                } catch (ExecutionException e) {
                    TComLogs.info(" BatchResultImpl.get(long timeout, TimeUnit unit) happened ExecutionException , msg : {}", e.getMessage());
                    throw new TransactionRunTimeException(e);
                } catch (TimeoutException e) {
                    TComLogs.info(" BatchResultImpl.get(long timeout, TimeUnit unit) happened TimeoutException , msg : {}", e.getMessage());
                    throw new TransactionRunTimeException(e);
                } catch (Exception e) {
                    TComLogs.info(" BatchResultImpl.get(long timeout, TimeUnit unit) happened Exception , msg : {}", e.getMessage());
                    throw new TransactionRunTimeException(e);
                } finally {
                    count.countDown();
                }
            }
        });
        count.await(timeout, unit);
        return datas;
    }

    public String getSessionId() {
        return sessionId;
    }

    public List<BatchItemResult<V>> getBatchItemResults() {
        return batchItemResults;
    }
}
