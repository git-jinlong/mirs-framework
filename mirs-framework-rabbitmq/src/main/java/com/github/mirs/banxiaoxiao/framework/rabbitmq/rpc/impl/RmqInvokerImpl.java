package com.github.mirs.banxiaoxiao.framework.rabbitmq.rpc.impl;

import com.github.mirs.banxiaoxiao.framework.common.util.UUID;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgChannelType;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgPublisher;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.MsgPublisherFactory;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.annotation.Subscribe;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.annotation.Subscribe.Type;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rpc.RmqInvoker;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author zrh
 * @title: RmqInvokerImpl
 * @projectName mqrpc
 * @description: MQ 面向事件的远程调用
 * @date 2019/8/14 10:13
 * <p>
 * RmqInvoker--------Queue---------->RmqHandler
 * |
 * |
 * RmqInvoker<----------P2P--------------|
 */

public class RmqInvokerImpl<R> implements RmqInvoker<R> {
    @Resource
    private MsgPublisherFactory msgPublisherFactory;
    private final ConcurrentHashMap<String, RmqSession<R, ?>> map = new ConcurrentHashMap<>();

    /**
     * @param request Rpc请求
     * @param timeout 超时时间
     * @param <V>     请求返回值类型
     * @return 请求结果
     */
    @Override
    public <V> V invoke(R request, long timeout) throws TimeoutException, InterruptedException {
        // 用RmqRequest包装参数request
        String requestId = UUID.random19();
        RmqRequest<R> rmqRequest = new RmqRequest<>(request);
        rmqRequest.setRequestId(requestId);
        // 创建session，保存在map中
        RmqSession<R, V> rmqSession = new RmqSession<>();
        rmqSession.setId(requestId);
        rmqSession.setRmqRequest(rmqRequest);
        map.put(requestId, rmqSession);
        // 发送消息到Rpc Handler
        MsgPublisher msgPublisher = msgPublisherFactory.getMsgPublisher(MsgChannelType.QUEUE, rmqRequest.getClass());
        msgPublisher.publish(rmqRequest);
        try {
            rmqSession.getMutex().lockTimeOut(timeout, TimeUnit.MILLISECONDS);
            // 如果没有超时则返回结果
            return (V) map.get(requestId).getData();
        } finally {
            // 清除Map中的该数据
            map.remove(requestId);
        }
    }

    @Override
    public <V> Future<V> invokeAsyn(R request) {
        // 用RmqRequest包装参数request
        String requestId = UUID.random19();
        RmqRequest<R> rmqRequest = new RmqRequest<>(request);
        rmqRequest.setRequestId(requestId);
        // 创建session，保存在map中
        RmqSession<R, V> rmqSession = new RmqSession<>();
        rmqSession.setId(requestId);
        rmqSession.setRmqRequest(rmqRequest);
        map.put(requestId, rmqSession);
        // 发送消息到Rpc Handler
        MsgPublisher msgPublisher = msgPublisherFactory.getMsgPublisher(MsgChannelType.QUEUE, rmqRequest.getClass());
        msgPublisher.publish(rmqRequest);
        CompletableFuture<V> future = new RmqCompletableFuture<>(requestId);
        rmqSession.setFuture(future);
        return future;
    }

    // 监听发回的P2P消息
    @Subscribe(Type.P2P)
    public void onRmqResponse(RmqResponse rmqResponse) {
        System.out.println("rmqResponse : " + rmqResponse);
        RmqSession<R, ?> rmqSession = map.get(rmqResponse.getId());
        if (Objects.isNull(rmqSession)) return;
        if (Objects.isNull(rmqSession.getFuture())) {// 同步
            rmqSession.setRmqResponse(rmqResponse);
            rmqSession.getMutex().unlock();
        } else {// 异步
            ((CompletableFuture) rmqSession.getFuture()).complete(rmqResponse.getMsg());
            map.remove(rmqResponse.getId());
        }
    }

    // 复写get方法处理失效数据
    class RmqCompletableFuture<V> extends CompletableFuture<V> {
        private String requestId;

        private RmqCompletableFuture(String requestId) {
            super();
            this.requestId = requestId;
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            try {
                return super.get();
            } finally {
                map.remove(requestId);
            }
        }

        @Override
        public V get(long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            try {
                return super.get(timeout, unit);
            } finally {
                map.remove(requestId);
            }
        }
    }
}
