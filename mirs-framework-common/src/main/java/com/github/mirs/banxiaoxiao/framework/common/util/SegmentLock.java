package com.github.mirs.banxiaoxiao.framework.common.util;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zcy 2018年10月19日
 */
public class SegmentLock {

    private Integer segments = 16;

    private final Object NULL = new Object();

    private final HashMap<Object, ReentrantLock> lockMap = new HashMap<>();

    public SegmentLock() {
        init(null, false);
    }

    public SegmentLock(Integer counts, boolean fair) {
        init(counts, fair);
    }

    private void init(Integer counts, boolean fair) {
        if (counts != null) {
            segments = counts;
        }
        for (int i = 0; i < segments; i++) {
            lockMap.put(i, new ReentrantLock(fair));
        }
    }

    public void lock(Object key) {
        if (key == null) {
            key = NULL;
        }
        int index = (key.hashCode() >>> 1) % segments;
        if (index < 0) {
            index = 0;
        }
        ReentrantLock lock = lockMap.get(index);
        lock.lock();
    }

    public void unlock(Object key) {
        if (key == null) {
            key = NULL;
        }
        int index = (key.hashCode() >>> 1) % segments;
        if (index < 0) {
            index = 0;
        }
        ReentrantLock lock = lockMap.get(index);
        lock.unlock();
    }
}