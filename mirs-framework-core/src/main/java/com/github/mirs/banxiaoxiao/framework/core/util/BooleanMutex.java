package com.github.mirs.banxiaoxiao.framework.core.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author zcy 2018年10月8日
 */
public class BooleanMutex {

    private Sync sync;

    public BooleanMutex() {
        sync = new Sync();
        set(false);
    }

    public void lock() throws InterruptedException {
        sync.innerLock();
    }

    public void lockTimeOut(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        sync.innerLock(unit.toNanos(timeout));
    }

    public void unlock() {
        set(true);
    }

    public void set(Boolean mutex) {
        if (mutex) {
            sync.innerSetTrue();
        } else {
            sync.innerSetFalse();
        }
    }

    public boolean state() {
        return sync.innerState();
    }

    private final class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = -7828117401763700385L;

        /**
         * 状态为1，则唤醒被阻塞在状态为FALSE的所有线程
         */
        private static final int TRUE = 1;

        /**
         * 状态为0，则当前线程阻塞，等待被唤醒
         */
        private static final int FALSE = 0;

        /**
         * 返回值大于0，则执行；返回值小于0，则阻塞
         */
        protected int tryAcquireShared(int arg) {
            return getState() == 1 ? 1 : -1;
        }

        /**
         * 实现AQS的接口，释放共享锁的判断
         */
        protected boolean tryReleaseShared(int ignore) {
            return true;
        }

        private boolean innerState() {
            return getState() == 1;
        }

        private void innerLock() throws InterruptedException {
            acquireSharedInterruptibly(0);
        }

        private void innerLock(long nanosTimeout) throws InterruptedException, TimeoutException {
            if (!tryAcquireSharedNanos(0, nanosTimeout))
                throw new TimeoutException();
        }

        private void innerSetTrue() {
            for (;;) {
                int s = getState();
                if (s == TRUE) {
                    return;
                }
                if (compareAndSetState(s, TRUE)) {
                    releaseShared(0);
                }
            }
        }

        private void innerSetFalse() {
            for (;;) {
                int s = getState();
                if (s == FALSE) {
                    return;
                }
                if (compareAndSetState(s, FALSE)) {
                    setState(FALSE);
                }
            }
        }
    }
}