package com.github.mirs.banxiaoxiao.framework.rabbitmq.batch;

/**
 * @author zcy 2019年8月8日
 */
public interface BatchTransaction {

    /**
     * @return
     */
    public String getTxid();

    /**
     * 发送事务消息
     * 
     * @param event
     * @return 返回事务条目序号,序号取值>0
     * @throws TransactionOverException
     */
    public int publish(Object event) throws TransactionOverException;

    /**
     * 提交批量事务，等待返回结果
     * 
     * @return
     * @throws TransactionOverException
     */
    public <V> BatchResult<V> commit() throws TransactionOverException;
}
