package com.github.mirs.banxiaoxiao.framework.dtask.executor.wrapper;

import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.DServiceBean;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.core.spring.SpringContextHolder;
import com.github.mirs.banxiaoxiao.framework.core.util.DataTimeWindow;
import com.github.mirs.banxiaoxiao.framework.core.util.DataTimeWindowListener;
import com.github.mirs.banxiaoxiao.framework.dtask.TaskException;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.executor.DccTaskExecutorEnv;
import com.github.mirs.banxiaoxiao.framework.dtask.executor.TaskExecutor;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @param <T>
 * @author zcy 2019年4月25日
 */
public class TaskExecutorDesk<T extends TaskConfig> extends DServiceBean<TaskExecutor<T>> implements TaskExecutor<T>,
        DataTimeWindowListener<TaskExecutorWrapper<T>> {

    /**
     *
     */
    private static final long serialVersionUID = -150456487587275474L;

    private DataTimeWindow<TaskExecutorWrapper<T>> taskExecutorWindow;

    private Class<TaskExecutor<T>> taskExecutorClass;

    private String taskCode;

    private String localServerId;

    public TaskExecutorDesk(Class<TaskExecutor<T>> taskExecutorClass, String taskCode, String localResId) {
        super();
        this.taskExecutorWindow = new DataTimeWindow<TaskExecutorWrapper<T>>(TimeUnit.MINUTES, 30, "dtask_" + taskCode, this);
        this.taskExecutorClass = taskExecutorClass;
        this.taskCode = taskCode;
        this.localServerId = localResId;
        setGroup(this.taskCode + "_" + this.localServerId);
        setInterface(TaskExecutor.class);
        setRef(this);
        getConfig().setRetries(0);
    }

    public void export() {
        super.export();
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized void start(T config) {
        TaskExecutorWrapper<T> query = new TaskExecutorWrapper<T>(this.taskExecutorClass, this.taskCode, config);
        TaskExecutorWrapper<T> exist = this.taskExecutorWindow.pick(query);
        TaskExecutorWrapper<T> current = query;
        if (exist != null) {
            current = exist;
        } else {
            TaskExecutor<T> delegate = SpringContextHolder.get().getAutowireCapableBeanFactory().createBean(this.taskExecutorClass);
            if (delegate instanceof DccTaskExecutorEnv) {
                DccTaskExecutorEnv<T> dccDelegate = (DccTaskExecutorEnv<T>) delegate;
                dccDelegate.setTaskCode(this.taskCode);
                dccDelegate.setTaskConfig(config);
            }
            current.setDelegate(delegate);
        }
        this.taskExecutorWindow.push(current);
        current.start(config);
    }

    @Override
    public synchronized int inspect(T config) {
        TaskExecutorWrapper<T> query = new TaskExecutorWrapper<T>(this.taskExecutorClass, this.taskCode, config);
        TaskExecutorWrapper<T> exist = this.taskExecutorWindow.pick(query);
        if (exist == null) {
            return TaskExecutor.INSPECT_STOPED;
        } else {
            this.taskExecutorWindow.push(exist);
            return exist.inspect(config);
        }
    }

    @Override
    public void kill(T config) throws TaskException {
        TaskExecutorWrapper<T> query = new TaskExecutorWrapper<T>(this.taskExecutorClass, this.taskCode, config);
        TaskExecutorWrapper<T> exist = this.taskExecutorWindow.pick(query);
        if (exist != null) {
            try {
                exist.kill(config);
            } catch (Throwable e) {
                // 如果停止异常则再放入时间窗，让其自动失效后强制kill
                this.taskExecutorWindow.push(exist);
                throw e;
            }
        }
    }

    @Override
    public synchronized void stop(T config) {
        TaskExecutorWrapper<T> query = new TaskExecutorWrapper<T>(this.taskExecutorClass, this.taskCode, config);
        TaskExecutorWrapper<T> exist = this.taskExecutorWindow.pick(query);
        if (exist != null) {
            try {
                exist.stop(config);
            } catch (Throwable e) {
                // 如果停止异常则再放入时间窗，让其自动失效后强制kill
                this.taskExecutorWindow.push(exist);
                throw e;
            }
        }
    }

    @Override
    public synchronized void onInvalid(List<TaskExecutorWrapper<T>> data) {
        for (TaskExecutorWrapper<T> taskwrapper : data) {
            boolean needFinallyPushWindows = true;
            try {
                int result = taskwrapper.onLostControlInspect();
                if (result != TaskExecutor.INSPECT_STOPED) {
                    needFinallyPushWindows = true;
                } else {
                    needFinallyPushWindows = false;
                }
                TComLogs.info("task executor [{}] lost of server inspect, interrupted result [{}]", taskwrapper, result);
            } catch (Throwable e) {
                TComLogs.error("task executor [{}] lost of server inspect, interrupted it error", e, taskwrapper);
            } finally {
                if (needFinallyPushWindows) {
                    this.taskExecutorWindow.push(taskwrapper);
                }
            }
        }
    }
}
