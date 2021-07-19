package com.github.mirs.banxiaoxiao.framework.dtask.executor.wrapper;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.executor.BaseTaskExecutor;
import com.github.mirs.banxiaoxiao.framework.dtask.executor.TaskExecutor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @param <T>
 * @author zcy 2019年4月25日
 */
public class TaskExecutorWrapper<T extends TaskConfig> extends BaseTaskExecutor<T> {

    private TaskExecutor<T> delegate;

    private Class<?> TaskExecutorClass;

    public TaskExecutorWrapper(Class<?> TaskExecutorClass, String taskCode, T config) {
        super(taskCode);
        setClazz(TaskExecutorWrapper.class);
        setTaskConfig(config);
        this.TaskExecutorClass = TaskExecutorClass;
        Class<?> slefClazz = this.TaskExecutorClass;
        while (slefClazz != Object.class) {
            Type t = slefClazz.getGenericSuperclass();
            if (t instanceof ParameterizedType) {
                Type[] args = ((ParameterizedType) t).getActualTypeArguments();
                if (args[0] instanceof Class) {
                    super.setConfigModelClass((Class<?>) args[0]);
                    break;
                }
            }
            slefClazz = slefClazz.getSuperclass();
        }
    }

    public void setDelegate(TaskExecutor<T> delegate) {
        this.delegate = delegate;
    }

    public int onLostControlInspect() {
        // 如果失去了和控制器的巡检信号，则进入自检程序，主要是判断任务是否转移到其他执行器上了。如果转移到了其他执行器上则自行了断
        TaskExecutorWrapper<T> dccTaskInfo = readFromDcc();
        T config = dccTaskInfo == null ? null : dccTaskInfo.getTaskConfig();
        if (dccTaskInfo == null || config == null || !dccTaskInfo.getServerId().equals(getServerId())) {
            TComLogs.info("task [{}] lost control inspect, task are no running on this server, kill it", dccTaskInfo);
            this.delegate.kill(config);
            return TaskExecutor.INSPECT_STOPED;
        } else if (!config.isEffective()) {
            TComLogs.info("task [{}] lost control inspect, task invalid, stop it", dccTaskInfo);
            this.delegate.stop(config);
        }
        int result = this.delegate.inspect(config);
        return result;
    }

    @Override
    public void start(T config) {
        this.delegate.start(config);
    }

    @Override
    public void stop(T config) {
        this.delegate.stop(config);
    }

    @Override
    public int inspect(T config) {
        return this.delegate.inspect(config);
    }

    @Override
    protected String doStart(T config) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doStop(String localPrimarykey, T config) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void kill(String localPrimarykey) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean isExistLocal(String localPrimarykey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return delegate == null ? null : delegate.toString();
    }
}
