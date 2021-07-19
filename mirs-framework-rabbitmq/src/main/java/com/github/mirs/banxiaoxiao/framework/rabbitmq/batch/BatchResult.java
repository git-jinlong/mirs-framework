package com.github.mirs.banxiaoxiao.framework.rabbitmq.batch;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 
 * @author zcy 2019年8月8日
 * @param <V>
 */
public interface BatchResult<V> extends Future<List<V>> {

    /**
     * 获取事务id
     * 
     * @return
     */
    public String getTxid();

    /**
     * 获取批量业务所有处理结果的子item结果集合
     * 
     * @return 返回不为空的List集合
     */
    public List<BatchItemResult<V>> getItemResults();

    /**
     * @param index
     * @return
     */
    public BatchItemResult<V> getItemResult(int index);
}
