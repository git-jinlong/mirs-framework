package com.github.mirs.banxiaoxiao.framework.core.cross;

import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;

/**
 * @Auther: lxj
 * @Date: 2020/2/28 14:27
 * @Description:
 */
public interface CDSResultProxy {

    /**
     * 功能描述: cross处理
     */
    public Result invoke(Invoker<?> invoker, Invocation invocation);
}
