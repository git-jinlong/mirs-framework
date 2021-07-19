package com.github.mirs.banxiaoxiao.framework.rabbitmq.batch;

import java.util.concurrent.Future;

/**
 * 批量任务每个条目的处理结果对象
 * 
 * @author zcy 2019年8月8日
 */
public interface BatchItemResult<V> extends Future<V> {

    /**
     * 返回条目index，和业务放发送消息的顺序一致
     * 
     * @return
     */
    public long getIndex();
    
}
