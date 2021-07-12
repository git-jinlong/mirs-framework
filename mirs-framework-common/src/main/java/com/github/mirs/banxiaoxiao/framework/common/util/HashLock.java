package com.github.mirs.banxiaoxiao.framework.common.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @param <T>
 * @author zcy 2018年10月19日
 */
public class HashLock {

    private boolean isFair = false;

    private final Object NULL = new Object();

    private final SegmentLock segmentLock = new SegmentLock();// 分段锁

    private final ConcurrentHashMap<Object, CountLock> lockMap = new ConcurrentHashMap<>();

    public HashLock() {
    }

    public HashLock(boolean fair) {
        isFair = fair;
    }

    public void lock(Object key) {
        if (key == null) {
            key = NULL;
        }
        CountLock lockInfo;
        segmentLock.lock(key);
        try {
            lockInfo = lockMap.get(key);
            if (lockInfo == null) {
                lockInfo = new CountLock(isFair);
                lockMap.put(key, lockInfo);
            }
        } finally {
            segmentLock.unlock(key);
        }
        lockInfo.lock();
        lockInfo.incrementAndGet();
    }

    public void unlock(Object key) {
        if (key == null) {
            key = NULL;
        }
        CountLock lockInfo = lockMap.get(key);
        if (lockInfo.get() == 1) {
            segmentLock.lock(key);
            try {
                if (lockInfo.get() == 1) {
                    lockMap.remove(key);
                }
            } finally {
                segmentLock.unlock(key);
            }
        }
        lockInfo.decrementAndGet();
        lockInfo.unlock();
    }

    private static class CountLock {

        private ReentrantLock lock;

        private AtomicInteger count = new AtomicInteger(1);

        private CountLock(boolean fair) {
            this.lock = new ReentrantLock(fair);
        }

        public void lock() {
            this.lock.lock();
        }

        public void unlock() {
            this.lock.unlock();
        }

        public int incrementAndGet() {
            return this.count.incrementAndGet();
        }

        public int decrementAndGet() {
            return this.count.decrementAndGet();
        }

        public int get() {
            return this.count.get();
        }
    }
}