package com.github.mirs.banxiaoxiao.framework.rabbitmq.batch.impl;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述: 线程工具类
 * @auther: lxj 2019/8/19 15:23
 */
public class TaskExecutorsHandle {

    private final static long TIME_OUT_DATA = 60l;
    private static ListeningExecutorService executorService;

    private synchronized static void init(long timeout, TimeUnit timeUnit){
        if(Objects.isNull(executorService) || executorService.isShutdown()){
            executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
            MoreExecutors.addDelayedShutdownHook(executorService, timeout, timeUnit);
        }
    }

    public synchronized static <T> Future<T> submit(Callable<T> task){
        init(TIME_OUT_DATA,TimeUnit.SECONDS);
        return executorService.submit(task);
    }

    public synchronized static Future<?> submit(Runnable task){
        init(TIME_OUT_DATA,TimeUnit.SECONDS);
        return executorService.submit(task);
    }

    public synchronized static <T> Future<T> submit(Runnable task, T result){
        init(TIME_OUT_DATA,TimeUnit.SECONDS);
        return executorService.submit(task,result);
    }

    public synchronized static void execute(Runnable task){
        init(TIME_OUT_DATA,TimeUnit.SECONDS);
        executorService.execute(task);
    }
}
