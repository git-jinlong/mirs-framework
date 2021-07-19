package com.github.mirs.banxiaoxiao.framework.rabbitmq.rpc.impl;

import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.router.MsgKeyHandler;

import java.io.Serializable;

/**
 * @author zrh
 * @title: RpcResponse
 * @projectName mqrpc
 * @description: 统一封装响应请求
 * @date 2019/8/14 9:50
 */
public class RmqResponse<P> implements Serializable , MsgKeyHandler {
  // 包装的消息，必须实现Serializable
  private P msg;
  private String  id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public P getMsg() {
    return msg;
  }

  public void setMsg(P msg) {
    this.msg = msg;
  }

  @Override
  public String getKey() {
    return this.getId();
  }
}
