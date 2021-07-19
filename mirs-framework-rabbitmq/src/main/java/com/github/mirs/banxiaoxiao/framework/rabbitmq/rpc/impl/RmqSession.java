package com.github.mirs.banxiaoxiao.framework.rabbitmq.rpc.impl;

import com.github.mirs.banxiaoxiao.framework.core.util.BooleanMutex;

import java.util.Objects;
import java.util.concurrent.Future;

/**
 * @author zrh
 * @title: RmqSession
 * @projectName bee-rmq
 * @description: 存储在Map的封装请求和响应的类
 * @date 2019/8/16 11:20
 */
public class RmqSession<R, P> {
    // 唯一消息请求id
    private String id;
    // 请求消息
    private RmqRequest<R> rmqRequest;
    // 响应消息
    private RmqResponse<P> rmqResponse;
    //真实返回数据
    private P data;
    // 同步超时控制锁
    private BooleanMutex mutex = new BooleanMutex();
    // 异步计算结果
    private Future future = null;

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }

    public BooleanMutex getMutex() {
        return mutex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RmqRequest<R> getRmqRequest() {
        return rmqRequest;
    }

    public void setRmqRequest(RmqRequest<R> rmqRequest) {
        this.rmqRequest = rmqRequest;
    }

    public P getData() {
        return data;
    }

    public void setData(P data) {
        this.data = data;
    }

    public RmqResponse<P> getRmqResponse() {
        return rmqResponse;
    }

    public void setRmqResponse(RmqResponse<P> rmqResponse) {
        this.rmqResponse = rmqResponse;
        if (Objects.nonNull(rmqResponse)) this.data = rmqResponse.getMsg();
    }

}
