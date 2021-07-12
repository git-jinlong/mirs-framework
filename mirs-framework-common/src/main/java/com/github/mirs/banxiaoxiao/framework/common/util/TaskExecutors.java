package com.github.mirs.banxiaoxiao.framework.common.util;

import java.util.concurrent.*;

public class TaskExecutors {

    private static ScheduledExecutorService pool;
    static {
        int taskThreads = 4;
        String threadSize = System.getProperty("arcvideo.taskthread");
        if (!StringUtil.isBlank(threadSize)) {
            try {
                taskThreads = Integer.parseInt(threadSize);
            } catch (Exception e) {
            }
        }
        pool = Executors.newScheduledThreadPool(taskThreads);
    }

    public static ScheduledFuture<?> submit(Runnable task, int initDelay, int delay, TimeUnit unit) {
        return pool.scheduleWithFixedDelay(task, initDelay, delay, unit);
    }

    public static Future<?> submit(Runnable task) {
        return pool.submit(task);
    }

    public static <V> Future<V> submit(Callable<V> task) {
        return pool.submit(task);
    }
}
