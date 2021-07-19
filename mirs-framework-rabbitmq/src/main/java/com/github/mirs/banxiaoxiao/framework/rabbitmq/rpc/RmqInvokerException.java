package com.github.mirs.banxiaoxiao.framework.rabbitmq.rpc;

/**
 * @author zrh
 * @title: RmqInvokerException
 * @projectName mqrpc
 * @description: 检查是否实现了Serialization接口
 * @date 2019/8/14 11:25
 */
public class RmqInvokerException extends RuntimeException {

  public RmqInvokerException() {
  }

  public RmqInvokerException(String message) {
    super(message);
  }

  public RmqInvokerException(String message, Throwable cause) {
    super(message, cause);
  }

  public RmqInvokerException(Throwable cause) {
    super(cause);
  }
}
