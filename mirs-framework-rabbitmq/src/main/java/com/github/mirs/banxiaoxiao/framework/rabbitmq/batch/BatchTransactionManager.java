package com.github.mirs.banxiaoxiao.framework.rabbitmq.batch;


/**
 * 具有批量事务能力的消息发生器，在多个消息发送完成后等待消费方处理完后同时返回处理结果
 * 
 * @author zcy 2019年8月8日
 */
public interface BatchTransactionManager {

    /**
     * 开启一个批量事务
     * 
     * @return 
     */
    public BatchTransaction startBatchTransaction(Class<?> requestMsgType);

}
