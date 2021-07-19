package com.github.mirs.banxiaoxiao.framework.rabbitmq.rpc;

import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * @author zcy
 * @title: RmqInvoker
 * @projectName mqrpc
 * @description:
 * @date 2019/8/14 10:00
 */
public interface RmqInvoker<R> {
  /**
   * 同步调用
   *
   * @param request
   * @param timeout
   * @return
   */
  public <V> V invoke(R request, long timeout) throws TimeoutException, InterruptedException;

  /**
   * 异步调用
   *
   * @param request
   * @return
   */
  public <V> Future<V> invokeAsyn(R request);
}
