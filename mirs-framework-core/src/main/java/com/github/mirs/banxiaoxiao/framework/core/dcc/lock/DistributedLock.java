package com.github.mirs.banxiaoxiao.framework.core.dcc.lock;
 
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;
import com.github.mirs.banxiaoxiao.framework.core.dcc.DccException;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
 
/**
 * 基于zk的分布式锁实现，目前只支持全平台全局锁，不能应用在持续并发高的场景中！
 * 
 * @author zcy 2018年9月7日
 */
@Deprecated
public class DistributedLock {
 
    private static final int DEFAULT_TIMEOUT_PERIOD = 60000;
 
    private static final byte[] data = {0x12, 0x34};
 
    private String root = "/bee/locks";
 
    private String id;
 
    private LockId selfId;
 
    private String winnerId;
 
    private String lastChildId;
 
    private DccClient dccClient;
    
    private String lockName ;
    
    public DistributedLock(DccClient dccClient, String name) {
    	this.dccClient = dccClient;
    	this.lockName = name;
    	this.dccClient.writeData(this.root, "");
    }
    
    private ZooKeeper getZk() {
    	return this.dccClient.getZk();
    }
 
    /**
     * 尝试获取锁操作，阻塞式可被中断
     */
    public void lock() throws LockException {
        if (isOwner()) {
            return;
        }
 
        BooleanMutex mutex = new BooleanMutex();
        acquireLock(mutex);
        // 避免getZk()重启后导致watcher丢失，会出现死锁使用了超时进行重试
        try {
            mutex.lockTimeOut(DEFAULT_TIMEOUT_PERIOD, TimeUnit.MILLISECONDS);// 阻塞等待值为true
        } catch (Exception e) {
            e.printStackTrace();
            if (!mutex.state()) {
                lock();
            }
        }
    }
 
    public boolean tryLock()  {
        if (isOwner()) { // 锁重入
            return true;
        }
        acquireLock(null);
        return isOwner();
    }
 
    public void unlock() {
        if (id != null) {
            try {
            	this.dccClient.delete(root + "/" + id);
            } catch(DccException e){
            	throw e;
            } catch(Throwable e){
            	throw new LockException(e);
            } finally {
                id = null;
            }
        } else {
            //do nothing
        }
    }
 
    public String getRoot() {
        return root;
    }
 
    public boolean isOwner() {
        return id != null && winnerId != null && id.equals(winnerId);
    }
 
    public String getId() {
        return this.id;
    }
 
    private Boolean acquireLock(final BooleanMutex mutex) {
        try {
            do {
                if (id == null) { 
                    String path = getZk().create(root + "/" + lockName, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                    int index = path.lastIndexOf("/");
                    id = StringUtil.substring(path, index + 1);
                    selfId = new LockId(id);
                }
 
                if (id != null) {
                    List<String> names = getZk().getChildren(root, false);
                    if (names.isEmpty()) {
                        id = null; 
                    } else {
                        SortedSet<LockId> sortedNames = new TreeSet<>();
                        for (String name : names) {
                            sortedNames.add(new LockId(name));
                        }
 
                        if (!sortedNames.contains(selfId)) {
                            id = null;
                            continue;
                        }
                        winnerId = sortedNames.first().getName();
                        if (mutex != null && isOwner()) {
                            mutex.unlock();
                            return true;
                        } else if (mutex == null) {
                            return isOwner();
                        }
 
                        SortedSet<LockId> lessThanMe = sortedNames.headSet(selfId);
                        if (!lessThanMe.isEmpty()) {
                            LockId lastChildName = lessThanMe.last();
                            lastChildId = lastChildName.getName();
                            Stat stat = getZk().exists(root + "/" + lastChildId, new Watcher() {
                                public void process(WatchedEvent event) {
                                    acquireLock(mutex);
                                }
                            });
                            if (stat == null) {
                                acquireLock(mutex);
                            }
                        } else {
                            if (isOwner()) {
                                mutex.unlock();
                            } else {
                                id = null;
                            }
                        }
                    }
                }
            } while (id == null);
        } catch (KeeperException e) {
            if (mutex != null) {
                mutex.unlock();
            }
        } catch (InterruptedException e) {
            if (mutex != null) {
                mutex.unlock();
            }
        } catch (Throwable e) {
            if (mutex != null) {
                mutex.unlock();
            }
        }
 
        if (isOwner() && mutex != null) {
            mutex.unlock();
        }
        return Boolean.FALSE;
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
     
        public void unlock(){
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
}
