package com.github.mirs.banxiaoxiao.framework.redis.config.lock;

import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @author: bc
 * @date: 2021-03-29 14:12
 **/
public interface DistributedLock {


    RLock lock(String lockKey);

    RLock lock(String lockKey, int timeout);

    RLock lock(String lockKey, TimeUnit unit, int timeout);

    boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime);

    void unlock(String lockKey);

    void unlock(RLock lock);


}
