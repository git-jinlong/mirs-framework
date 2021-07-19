package com.github.mirs.banxiaoxiao.framework.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @param <T>
 * @author zcy 2018年10月12日
 */
public class DataTimeWindow<T> {

    private TimeUnit unit;

    protected int length;

    protected int checkCursor;

    protected int dataCursor;

    protected WindowBlock<T>[] blocks;

    private Timer timer;

    private DataTimeWindowListener<T> listener;

    private boolean emptyNotify = false;

    public void setListener(DataTimeWindowListener<T> listener) {
        this.listener = listener;
    }

    public DataTimeWindow(TimeUnit unit, int length) {
        this(unit, length, null, null);
    }

    public DataTimeWindow(TimeUnit unit, int length, String name) {
        this(unit, length, name, null);
    }

    public DataTimeWindow(TimeUnit unit, int length, DataTimeWindowListener<T> listener) {
        this(unit, length, null, listener);
    }

    @SuppressWarnings("unchecked")
    public DataTimeWindow(TimeUnit unit, int length, String name, DataTimeWindowListener<T> listener) {
        if (length < 1) {
            throw new IllegalArgumentException("window length must greater than 1");
        }
        if (name == null) {
            this.timer = new Timer();
        } else {
            this.timer = new Timer(name);
        }
        this.listener = listener;
        this.unit = unit;
        this.length = length;
        this.timer.schedule(new TimerTask() {

            public void run() {
                polling();
            }
        }, unit.toMillis(1), unit.toMillis(1));
        this.checkCursor = 1;
        this.dataCursor = 0;
        this.blocks = new WindowBlock[length];
        for (int i = 0; i < length; i++) {
            this.blocks[i] = new WindowBlock<T>();
        }
    }

    public boolean isAlive() {
        return this.timer != null;
    }

    public void destory() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        for (int i = 0; i < this.length; i++) {
            WindowBlock<T> windowBlock = this.blocks[i];
            windowBlock.clear();
        }
        this.checkCursor = 1;
        this.dataCursor = 0;
    }

    public boolean isEmptyNotify() {
        return emptyNotify;
    }

    public void setEmptyNotify(boolean emptyNotify) {
        this.emptyNotify = emptyNotify;
    }

    protected void polling() {
        if (listener != null && (isEmptyNotify() || blocks[checkCursor].data.size() > 0)) {
            try {
                listener.onInvalid(new ArrayList<>(blocks[checkCursor].data.keySet()));
            } catch (Throwable e) {
            }
        }
        blocks[checkCursor].clear();
        checkCursor++;
        if (checkCursor >= this.length) {
            checkCursor = 0;
        }
        dataCursor++;
        if (dataCursor >= this.length) {
            dataCursor = 0;
        }
    }

    public List<T> gets() {
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < this.length; i++) {
            WindowBlock<T> windowBlock = this.blocks[i];
            list.addAll(windowBlock.data.keySet());
        }
        return list;
    }

    public void push(T data) {
        if (!isAlive()) {
            throw new IllegalStateException("invalid time window");
        }
        if (data == null) {
            throw new NullPointerException();
        }
        blocks[dataCursor].push(data);
    }

    /**
     * 查找数据是否存在时间窗内，根据equals方法来比较的
     *
     * @param data
     * @return
     */
    public T find(T data) {
        if (data == null) {
            return null;
        }
        for (int i = 0; i < this.length; i++) {
            WindowBlock<T> windowBlock = this.blocks[i];
            T exist = windowBlock.find(data);
            if (exist != null) {
                return exist;
            }
        }
        return null;
    }

    /**
     * 查找第一个匹配的数据并从时间窗内移除，根据equals方法来比较的
     *
     * @param data
     * @return
     */
    public T pick(T data) {
        if (!isAlive()) {
            throw new IllegalStateException("invalid time window");
        }
        if (data == null) {
            return null;
        }
        for (int i = 0; i < this.length; i++) {
            WindowBlock<T> windowBlock = this.blocks[i];
            T exist = windowBlock.remove(data);
            if (exist != null) {
                return exist;
            }
        }
        return null;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public int getLength() {
        return length;
    }

    protected static class WindowBlock<T> {

        private ConcurrentHashMap<T, T> data = new ConcurrentHashMap<T, T>();

        public void push(T data) {
            this.data.put(data, data);
        }

        public void clear() {
            this.data.clear();
        }

        public T find(T obj) {
            return this.data.get(obj);
        }

        public T remove(T obj) {
            return this.data.remove(obj);
        }
    }
}
