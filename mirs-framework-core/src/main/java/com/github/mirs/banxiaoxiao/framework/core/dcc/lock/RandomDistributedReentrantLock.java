package com.github.mirs.banxiaoxiao.framework.core.dcc.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;

/**
 * @author zcy 2018年9月29日
 */
public class RandomDistributedReentrantLock extends RandomDistributedLock {

    private ReentrantLock reentrantLock = new ReentrantLock();

    public RandomDistributedReentrantLock(String key) {
        this(null, key);
    }
    
    public RandomDistributedReentrantLock(DccClient dccClient, String name) {
        super(dccClient, name);
    }

    public void lock() {
        reentrantLock.lock();
        super.lock();
    }

    public void lock(int timeout, TimeUnit unit) throws LockException, TimeoutException {
        boolean isTimeout = false;
        try {
            boolean success = reentrantLock.tryLock(timeout, unit);
            if (!success) {
                isTimeout = true;
            }
        } catch (InterruptedException e) {
            isTimeout = true;
        }
        if (isTimeout) {
            throw new TimeoutException();
        }
        super.lock(timeout, unit);
    }

    public void unlock() {
        try {
            super.unlock();
        } finally {
            reentrantLock.unlock();
        }
    }
}
