package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq;

public abstract class RunnablePriority implements Runnable, Comparable<RunnablePriority> {

    private int priority;

    public int getPriority() {
        return priority;
    }

    public RunnablePriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(RunnablePriority o) {
        if (this.getPriority() < o.priority) {
            return 1;
        }
        if (this.getPriority() > o.priority) {
            return -1;
        }
        return 0;
    }

}