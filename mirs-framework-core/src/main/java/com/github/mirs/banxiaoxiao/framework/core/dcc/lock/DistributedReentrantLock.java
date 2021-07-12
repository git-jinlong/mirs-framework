package com.github.mirs.banxiaoxiao.framework.core.dcc.lock;
 
import java.text.MessageFormat;
import java.util.concurrent.locks.ReentrantLock;

import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;
 
/**
 * 多进程+多线程分布式锁
 */
@Deprecated
public class DistributedReentrantLock extends DistributedLock {
 
    private static final String ID_FORMAT = "Thread[{0}] Distributed[{1}]";
    
    private ReentrantLock reentrantLock = new ReentrantLock();
 
    public DistributedReentrantLock(DccClient dccClient, String name) {
        super(dccClient, name);
    }
 
    public void lock()  {
        reentrantLock.lock();
        super.lock();
    }
 
    public boolean tryLock() {
        return reentrantLock.tryLock() && super.tryLock();
    }
 
    public void unlock() {
        try {
            super.unlock();
        } finally {
            reentrantLock.unlock();
        }
    }
 
    @Override
    public String getId() {
        return MessageFormat.format(ID_FORMAT, Thread.currentThread().getId(), super.getId());
    }
 
    @Override
    public boolean isOwner() {
        return reentrantLock.isHeldByCurrentThread() && super.isOwner();
    }
}
