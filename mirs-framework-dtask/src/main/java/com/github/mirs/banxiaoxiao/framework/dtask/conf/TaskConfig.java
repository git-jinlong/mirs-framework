package com.github.mirs.banxiaoxiao.framework.dtask.conf;

import java.io.Serializable;

/**
 * 任务配置模型，该模型对象会被json 序列化存放在dcc中。后续任务启停和调度都会依赖该配置.
 * 
 * @author zcy 2019年3月18日
 */
public abstract class TaskConfig implements Serializable {

    private static final long serialVersionUID = -5142304135261921241L;

    private String taskId;

    private long lastUpdateTime;

    private int taskTimeout = 20000;

    public TaskConfig() {
    }

    public TaskConfig(String taskId) {
        super();
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getTaskTimeout() {
        return taskTimeout;
    }

    public void setTaskTimeout(int taskTimeout) {
        this.taskTimeout = taskTimeout;
    }

    /**
     * 任務是否生效.调度器会轮询config的状态，如果一旦生效调度器则会发起启动task的流程
     * 
     * @return
     */
    public abstract boolean isEffective();

    public String toString() {
        return getTaskId();
    }

    @Override
    public int hashCode() {
        return this.taskId == null ? 0 : this.taskId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TaskConfig)) {
            return false;
        }
        TaskConfig other = (TaskConfig) obj;
        return other.getTaskId().equals(getTaskId());
    }
}
