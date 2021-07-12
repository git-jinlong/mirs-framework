package com.github.mirs.banxiaoxiao.framework.core.dcc.lock;

import com.github.mirs.banxiaoxiao.framework.common.util.JsonUtils;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.common.util.UUID;
import com.github.mirs.banxiaoxiao.framework.core.dcc.AbstractDccApp;
import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;
import com.github.mirs.banxiaoxiao.framework.core.dcc.DccException;
import org.apache.zookeeper.*;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author zcy 2018年9月29日
 */
public class RandomDistributedLock extends AbstractDccApp {

    private static final int DEFAULT_TIMEOUT_PERIOD = 20000;

    private String lockKey;

    private String lockPath;

    private String ownerId;

    private AtomicInteger lockCount = new AtomicInteger();

    public RandomDistributedLock(String key) {
        this(null, key);
    }

    public RandomDistributedLock(DccClient dccClient, String key) {
        super("locks", dccClient, null);
        this.lockKey = key;
        this.lockPath = getRoot().concat(DccClient.NODE_PATH_SEPARATOR).concat(lockKey);
        this.ownerId = UUID.random19();
    }

    private ZooKeeper getZk() {
        return getDccClient().getZk();
    }

    public void lock() throws LockException {
        lockCount.incrementAndGet();
        BooleanMutex mutex = new BooleanMutex();
        acquireLock(this.lockPath, mutex, this.ownerId, DEFAULT_TIMEOUT_PERIOD, TimeUnit.MILLISECONDS);
    }

    public void lock(int timeout, TimeUnit unit) throws LockException, TimeoutException {
        lockCount.incrementAndGet();
        BooleanMutex mutex = new BooleanMutex();
        acquireLock(this.lockPath, mutex, this.ownerId, timeout, unit);
    }

    private void acquireLock(String path, BooleanMutex mutex, String ownerId, int timeout, TimeUnit unit) {
        try {
            getZk().create(path, JsonUtils.toJson(ownerId).getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            mutex.unlock();
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NODEEXISTS) {
                competeWhereExist(path, mutex, ownerId, timeout, unit);
            } else {
                throw new LockException(e);
            }
        } catch (Throwable e) {
            throw new LockException(e);
        }
    }

    private void competeWhereExist(String path, BooleanMutex mutex, String ownerId, int timeout, TimeUnit unit) {
        // 看看是不是自己
        String dccOwner = getDccClient().readData(path, String.class);
        if (StringUtil.equals(dccOwner, ownerId)) {
            mutex.unlock();
            return;
        }
        try {
            Stat stat = getZk().exists(path, new Watcher() {

                public void process(WatchedEvent event) {
                    acquireLock(path, mutex, ownerId, timeout, unit);
                }
            });
            if (stat == null) {
                acquireLock(path, mutex, ownerId, timeout, unit);
            } else {
                mutex.lockTimeOut(timeout, unit);
            }
        } catch (KeeperException e) {
            if (e.code() == KeeperException.Code.NONODE) {
                acquireLock(path, mutex, ownerId, timeout, unit);
                return;
            } else {
                throw new LockException(e);
            }
        } catch (Throwable e) {
            throw new LockException(e, path);
        }
    }

    public void unlock() {
        try {
            if (lockCount.decrementAndGet() == 0) {
                getDccClient().delete(this.lockPath);
            }
        } catch (DccException e) {
            throw e;
        } catch (Throwable e) {
            throw new LockException(e);
        }
    }

    static class BooleanMutex {

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
                for (; ; ) {
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
                for (; ; ) {
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
}
