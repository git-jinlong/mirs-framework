package com.github.mirs.banxiaoxiao.framework.dtask.executor;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.dtask.TaskException;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;

/**
 * @author zcy 2019年5月30日
 */
public abstract class BaseTaskExecutor<T extends TaskConfig> extends DccTaskExecutorEnv<T> implements TaskExecutor<T> {

    private String localPrimarykey;

    private boolean running = false;

    private int lastInspect;

    public BaseTaskExecutor() {
        this(null);
    }

    public BaseTaskExecutor(String taskCode) {
        this(null, taskCode);
    }

    public BaseTaskExecutor(T taskConfig, String taskCode) {
        super(taskConfig, taskCode);
    }

    private void flush(boolean oldRunning, long oldConfigTime, int oldLastInspect, String oldLocalPrimarykey) {
        boolean runChange = this.running != oldRunning;
        boolean configChange = this.getTaskConfig().getLastUpdateTime() != oldConfigTime;
        boolean inspectResultChange = this.lastInspect != oldLastInspect;
        if (runChange || configChange || inspectResultChange || !(StringUtil.equals(this.localPrimarykey, oldLocalPrimarykey))) {
            flushToDcc();
        }
    }

    @Override
    public synchronized void start(T config) {
        TComLogs.info("begin start task [{}]", this);
        boolean oldRunning = this.running;
        long oldConfigTime = this.getTaskConfig() == null ? 0 : this.getTaskConfig().getLastUpdateTime();
        int oldLastInspect = this.lastInspect;
        String oldLocalPrimarykey = this.localPrimarykey;
        setTaskConfig(config);
        this.running = true;
        TaskException exception = null;
        try {
            boolean isExisLocal = this.localPrimarykey != null;
            if (isExisLocal) {
                isExisLocal = isExistLocal(this.localPrimarykey);
            }
            if (!isExisLocal) {
                this.localPrimarykey = doStart(config);
                if (StringUtil.isBlank(this.localPrimarykey)) {
                    throw new TaskException("start task [" + this + "] exception, the primary key is null");
                }
            }
        } catch (TaskException e) {
            exception = e;
        } catch (Throwable e) {
            exception = new TaskException("start task [" + this + "] fail", e);
        }
        flush(oldRunning, oldConfigTime, oldLastInspect, oldLocalPrimarykey);
        if (exception != null) {
            throw exception;
        }
        TComLogs.info("started task [{}]", this);
    }

    @Override
    public synchronized void stop(T config) {
        TComLogs.info("begin stop task [{}]", this);
        if (config == null) {
            config = getTaskConfig();
        }
        boolean oldRunning = this.running;
        long oldConfigTime = this.getTaskConfig() == null ? 0 : this.getTaskConfig().getLastUpdateTime();
        int oldLastInspect = this.lastInspect;
        String oldLocalPrimarykey = this.localPrimarykey;
        setTaskConfig(config);
        this.running = false;
        TaskException exception = null;
        try {
            boolean isExisLocal = isExistLocal(this.localPrimarykey);
            if (!isExisLocal) {
                this.localPrimarykey = null;
            } else {
                doStop(this.localPrimarykey, config);
                if (isExistLocal(this.localPrimarykey)) {
                    throw new TaskException("stop task [" + this + "] fail, the local executor still exist");
                }
            }
        } catch (TaskException e) {
            exception = e;
        } catch (Throwable e) {
            exception = new TaskException("stop task [" + this + "] fail", e);
        }
        flush(oldRunning, oldConfigTime, oldLastInspect, oldLocalPrimarykey);
        if (exception != null) {
            throw exception;
        }
        TComLogs.info("stoped task [{}]", this);
    }

    @Override
    public synchronized int inspect(T config) {
        boolean needRestart = needRestart(getTaskConfig(), config);
        if (needRestart) {
            stop(config);
            start(config);
            return inspect(config);
        }
        if (config == null) {
            config = getTaskConfig();
        }
        if (config == null) {
            config = getTaskConfig();
        }
        boolean oldRunning = this.running;
        long oldConfigTime = this.getTaskConfig() == null ? 0 : this.getTaskConfig().getLastUpdateTime();
        int oldLastInspect = this.lastInspect;
        String oldLocalPrimarykey = this.localPrimarykey;
        setTaskConfig(config);
        TaskException exception = null;
        try {
            boolean isExisLocal = isExistLocal(this.localPrimarykey);
            if (isExisLocal) {
                if (running) {
                    lastInspect = TaskExecutor.INSPECT_RUNNING;
                } else {
                    lastInspect = retryStop(config);
                }
            } else {
                if (running) {
                    lastInspect = retryStart(config);
                } else {
                    lastInspect = TaskExecutor.INSPECT_STOPED;
                }
            }
        } catch (TaskException e) {
            exception = e;
        } catch (Throwable e) {
            exception = new TaskException("inspect task [" + this + "] fail", e);
        }
        flush(oldRunning, oldConfigTime, oldLastInspect, oldLocalPrimarykey);
        if (exception != null) {
            throw exception;
        }
        return lastInspect;
    }

    protected int retryStop(T config) {
        TComLogs.info("retry stop start task [{}]", this);
        stop(config);
        boolean isExisLocal = isExistLocal(this.localPrimarykey);
        if (isExisLocal) {
            kill(this.localPrimarykey);
        }
        isExisLocal = isExistLocal(this.localPrimarykey);
        if (isExisLocal) {
            TComLogs.warn("retry stop task [{}] process [{}] fail", config, this.localPrimarykey);
            return TaskExecutor.INSPECT_RETRY_STOP;
        } else {
            TComLogs.info("retry stop task [{}] process [{}] success", config, this.localPrimarykey);
            return TaskExecutor.INSPECT_STOPED;
        }
    }

    protected int retryStart(T config) {
        TComLogs.info("retry start start task [{}]", this);
        start(config);
        boolean isExisLocal = isExistLocal(this.localPrimarykey);
        if (isExisLocal) {
            TComLogs.info("retry start task [{}] process [{}] success", config, this.localPrimarykey);
            return TaskExecutor.INSPECT_RUNNING;
        } else {
            TComLogs.warn("retry start task [{}] process [{}] fail", config, this.localPrimarykey);
            return TaskExecutor.INSPECT_RETRY_START;
        }
    }

    public String getLocalPrimarykey() {
        return localPrimarykey;
    }

    public void kill(T config) throws TaskException {
        kill(this.localPrimarykey);
    }

    /**
     * 判断任务是否需要重启，有些情况下可能由于任务的配置项发生变化而需要重新启动任务，但是任务有效期本身是没有变化的
     *
     * @param oldConfig 可能為空
     * @param newConfig 可能為空
     * @return
     */
    protected boolean needRestart(T oldConfig, T newConfig) {
        return false;
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * 强杀本地任务
     *
     * @param localPrimarykey 本地任务唯一标识，如：本地任务为新进程就是进程ID、本地任务为线程则是线程Id等
     */
    protected abstract void kill(String localPrimarykey);

    /**
     * 判断本地任务是否真的在运行
     *
     * @param localPrimarykey
     * @return
     */
    protected abstract boolean isExistLocal(String localPrimarykey);

    /**
     * 启动任务，返回本地唯一主键（如：进程id、线程ID等）
     *
     * @param config
     * @return
     */
    protected abstract String doStart(T config);

    /**
     * 停止任务，config参数可能出现为null的情况。比如，配置信息被强制删除，这种情况下也任务是需要强制stop
     *
     * @param config
     */
    protected abstract void doStop(String localPrimarykey, T config);

    public String toString() {
        String msg = getTaskCode() + "," + getTaskId() + "," + getServerId() + "," + "isEffective="
                + (getTaskConfig() == null ? null : getTaskConfig().isEffective());
        return msg + ",running=" + isRunning() + ", primarykey=" + getLocalPrimarykey();
    }
}
