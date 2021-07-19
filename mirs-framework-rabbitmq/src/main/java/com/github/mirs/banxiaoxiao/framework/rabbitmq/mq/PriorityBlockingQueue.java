package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PriorityBlockingQueue<E extends Runnable> implements BlockingQueue<E> {

    private Map<Integer, Queue<E>> queueMap;

    private List<Integer> prioritys;

    private AtomicInteger count;

    private int defaultPriority = 0;

    private ReentrantLock lock = new ReentrantLock();

    private Condition empty = lock.newCondition();

    private Condition notEmpty = lock.newCondition();

    private int capacity;

    public PriorityBlockingQueue(List<Integer> prioritys, int capacity) {
        this.capacity = capacity;
        this.queueMap = new HashMap<Integer, Queue<E>>();
        if (prioritys == null) {
            this.prioritys = new ArrayList<Integer>();
            this.prioritys.add(defaultPriority);
        } else {
            this.prioritys = new ArrayList<Integer>(prioritys);
        }
        for (Integer p : this.prioritys) {
            LinkedList<E> queue = new LinkedList<E>();
            queueMap.put(p, queue);
        }
        this.prioritys.sort((o1, o2) -> (o2 - o1));
        this.count = new AtomicInteger();
    }

    @Override
    public int size() {
        return this.count.get();
    }

    @Override
    public boolean isEmpty() {
        return this.count.get() == 0;
    }

    protected E get() {
        for (int p : this.prioritys) {
            Queue<E> queue = getQueue(p);
            E data = queue.poll();
            if (data != null) {
                return data;
            }
        }
        return null;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        lock.lock();
        try {
            E data = get();
            if (data != null) {
                this.count.decrementAndGet();
                notEmpty.signal();
                return data;
            } else {
                empty.await(timeout, unit);
                return get();
            }
        } catch (Throwable e) {
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E take() throws InterruptedException {
        lock.lock();
        try {
            E data = null;
            while ((data = get()) == null) {
                empty.await();
            }
            this.count.decrementAndGet();
            notEmpty.signal();
            return data;
        } catch (Throwable e) {
            return take();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        lock.lock();
        try {
            while (this.capacity <= size()) {
                notEmpty.await();
            }
            boolean success = getQueue(e).offer(e);
            if (success) {
                this.count.incrementAndGet();
                empty.signal();
                return true;
            } else {
                return offer(e);
            }
        } catch (Throwable e1) {
            e1.printStackTrace();
            return false;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object task) {
        if (!(task instanceof RunnablePriority)) {
            return true;
        }
        lock.lock();
        try {
            E e = (E) task;
            boolean success = getQueue(e).remove(e);
            if (success) {
                this.count.decrementAndGet();
                notEmpty.signal();
            }
            return success;
        } catch (Throwable e1) {
            return false;
        } finally {
            lock.unlock();
        }
    }

    protected Queue<E> getQueue(int priority) {
        return this.queueMap.get(priority);
    }

    protected Queue<E> getQueue(E e) {
        if (!(e instanceof RunnablePriority)) {
            return getFristQueue();
        }
        return getQueue(((RunnablePriority) e).getPriority());
    }

    protected Queue<E> getFristQueue() {
        return getQueue(this.prioritys.get(0));
    }

    @Override
    public E element() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public E peek() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public E poll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public E remove() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean add(E e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean contains(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void put(E e) throws InterruptedException {
        // TODO Auto-generated method stub
    }

    @Override
    public int remainingCapacity() {
        // TODO Auto-generated method stub
        return 0;
    }
}
