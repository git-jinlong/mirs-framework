package com.github.mirs.banxiaoxiao.framework.core.woodpecker.cmd.bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

/**
 * @author zcy 2020年3月2日
 */
public class BeanManager {

    private static final BeanManager INSTANCE = new BeanManager();

    private Map<Integer, Bean> beans = new HashMap<Integer, Bean>();

    private AtomicInteger nextIndex = new AtomicInteger(0);

    public BeanManager() {
    }

    public synchronized int add(Object bean, String alias) {
        if (bean == null) {
            throw new NullPointerException(bean + " " + alias);
        }
        Bean tbean = getBean(alias);
        if (tbean != null) {
            throw new IllegalArgumentException("bean alias " + alias + " exist");
        }
        tbean = new Bean();
        int index = nextIndex.incrementAndGet();
        tbean.setTarget(bean);
        tbean.setAlias(alias);
        tbean.setIndex(index);
        beans.put(index, tbean);
        return index;
    }

    public synchronized Object getByAlias(String alias) {
        Bean tbean = getBean(alias);
        if (tbean == null) {
            return null;
        } else {
            return tbean.getTarget();
        }
    }

    public synchronized Object getByIndex(Integer index) {
        if (beans.containsKey(index)) {
            return beans.get(index).getTarget();
        } else {
            return null;
        }
    }

    public synchronized Object remove(Integer index) {
        nextIndex = new AtomicInteger(index);
        return beans.remove(index);
    }

    public synchronized Collection<Bean> getAll() {
        return beans.values();
    }

    protected Bean getBean(String alias) {
        if (StringUtil.isBlank(alias)) {
            return null;
        }
        for (Bean bean : beans.values()) {
            if (StringUtil.equals(alias, bean.getAlias())) {
                return bean;
            }
        }
        return null;
    }

    public static BeanManager getInstance() {
        return INSTANCE;
    }

    static class Bean {

        private Object target;

        private String alias;

        private int index;

        public Object getTarget() {
            return target;
        }

        public void setTarget(Object target) {
            this.target = target;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
