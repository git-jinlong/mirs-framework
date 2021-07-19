package com.github.mirs.banxiaoxiao.framework.rabbitmq.rpc.impl;

import com.github.mirs.banxiaoxiao.framework.common.util.NetworkUtil;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rpc.RmqInvokerException;

import java.io.Serializable;
import java.lang.management.ManagementFactory;

/**
 * @author zrh
 * @title: RpcRequest
 * @projectName mqrpc
 * @description: 统一封装调用请求
 * @date 2019/8/14 9:50
 */
public class RmqRequest<R> implements Serializable {
    // 包装的消息,必须实现Serializable接口
    private R msg;
    // 本机ip
    private String ip;
    // 请求消息id，作为唯一标识
    private String requestId;

    public RmqRequest() {
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public RmqRequest(R msg) {
        String pidAndHost = ManagementFactory.getRuntimeMXBean().getName();
        String pid = pidAndHost.substring(0, pidAndHost.indexOf("@"));
        this.ip = NetworkUtil.getLocalHost() + "_" + pid;
        this.msg = msg;
    }

    public R getMsg() {
        return msg;
    }

    public void setMsg(R msg) throws RmqInvokerException {
        this.msg = msg;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
