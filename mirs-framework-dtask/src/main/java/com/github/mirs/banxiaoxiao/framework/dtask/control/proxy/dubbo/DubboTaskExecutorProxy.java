package com.github.mirs.banxiaoxiao.framework.dtask.control.proxy.dubbo;

import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.DReferenceBean;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.core.spring.SpringContextHolder;
import com.github.mirs.banxiaoxiao.framework.dtask.TaskException;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.control.TaskControlProperties;
import com.github.mirs.banxiaoxiao.framework.dtask.control.proxy.TaskExecutorProxy;
import com.github.mirs.banxiaoxiao.framework.dtask.control.proxy.TaskInvokerException;
import com.github.mirs.banxiaoxiao.framework.dtask.executor.BaseTaskExecutor;
import com.github.mirs.banxiaoxiao.framework.dtask.executor.TaskExecutor;
import com.github.mirs.banxiaoxiao.framework.dtask.seres.ServerResource;

/**
 * 任务执行器调度端的执行代理
 *
 * @author zcy 2019年4月29日
 */
public class DubboTaskExecutorProxy<T extends TaskConfig> extends BaseTaskExecutor<T> implements TaskExecutorProxy<T> {

    private int inspectFailNum = 0;

    /**
     * 0:运行中; 1：启动异常; 2：停止异常; 3:已停止
     */
    private int state;

    transient private DReferenceBean<TaskExecutor<T>> reference;

    transient private TaskExecutor<T> proxy;

    transient private String taskId;

    public DubboTaskExecutorProxy() {
        super();
        setClazz(DubboTaskExecutorProxy.class);
    }

    public DubboTaskExecutorProxy(T taskConfig, TaskControlProperties dtaskConfig, ServerResource serverResource) {
        super(taskConfig, dtaskConfig.getTaskCode());
        this.taskId = taskConfig.getTaskId();
        setServerId(serverResource.getId());
        setClazz(DubboTaskExecutorProxy.class);
        setConfigModelClass(dtaskConfig.getConfigModelClass());
    }

    public void setTaskConfig(T taskConfig) {
        super.setTaskConfig(taskConfig);
        this.taskId = taskConfig.getTaskId();
    }

    @Override
    public void setTaskControlProperties(TaskControlProperties dtaskProperties) {
        super.setTaskCode(dtaskProperties.getTaskCode());
        super.setConfigModelClass(dtaskProperties.getConfigModelClass());
        super.setClazz(dtaskProperties.getTaskExecutorProxyClass());
    }

    @Override
    public void setServerResource(ServerResource serverResource) {
        setServerId(serverResource.getId());
    }

    @Override
    public void init() {
        if (this.reference == null) {
            this.reference = new DReferenceBean<TaskExecutor<T>>();
            this.reference.setGroup(getTaskCode() + "_" + getServerId());
            this.reference.setInterface(TaskExecutor.class);
            this.reference.setApplicationContext(SpringContextHolder.get());
            this.reference.setRpcConfig("retries=0;timeout=" + getTaskConfig().getTaskTimeout());
            try {
                this.reference.afterPropertiesSet();
            } catch (Exception e) {
                throw new TaskException("init task executor proxy reference fail", e);
            }
        }
    }

    public boolean isRunning() {
        return state == 0;
    }

    public boolean isStoped() {
        return state == 3;
    }

    public boolean isStartException() {
        return state == 1;
    }

    public boolean isStopException() {
        return state == 2;
    }

    public synchronized void start() {
        try {
            init();
            if (isOnline()) {
                return;
            }
            get().start(getTaskConfig());
            this.state = 0;
            TComLogs.info("start task [{}] success", this);
        } catch (TaskInvokerException e) {
            throw e;
        } catch (Throwable e) {
            this.state = 1;
            throw new TaskInvokerException("start task [" + this + "] fail", e);
        }
    }

    private TaskExecutor<T> get() {
        if (this.proxy == null) {
            this.proxy = this.reference.get();
        }
        return this.proxy;
    }

    public synchronized void stop() {
        try {
            if (reference == null) {
                return;
            }
            get().stop(getTaskConfig());
            destory();
            this.state = 3;
            TComLogs.info("stop task [{}] success", this);
        } catch (TaskInvokerException e) {
            throw e;
        } catch (Throwable e) {
            this.state = 2;
            throw new TaskInvokerException("stop task [" + this + "] fail", e);
        }
    }

    public synchronized boolean isOnline() {
        try {
            if (this.reference == null) {
                return false;
            }
            int result = inspect();
            return result == TaskExecutor.INSPECT_RUNNING;
        } catch (TaskInvokerException e) {
            throw e;
        } catch (Throwable e) {
            throw new TaskInvokerException("inspect task [" + this + "] online fail", e);
        }
    }

    public synchronized int inspect() {
        try {
            int result = get().inspect(getTaskConfig());
            this.inspectFailNum = 0;
            return result;
        } catch (Throwable e) {
            this.inspectFailNum++;
            throw new TaskInvokerException("inspect task [" + this + "] fail", e);
        }
    }

    /**
     * <pre>
     * 巡检一下任务状态，返回值定义如下
     * 0 : 任务没有任何变化;
     * 1 : 任务正确停止了;
     * 2 : 任务状态发生了变化;
     * </pre>
     *
     * @param taskConfig
     * @return
     */
    public synchronized int inspect(T taskConfig) {
        boolean online = false;
        try {
            if (taskConfig != null) {
                setTaskConfig(taskConfig);
            }
            online = isOnline();
            // 看看任务状态是否超时了
            // 任务配置不存在了或者任务已经失效则stop并移除
            if (online && (taskConfig == null || !taskConfig.isEffective())) {
                stop();
                TComLogs.info("inspect task executor [{}], config not exist or not effective stop it", taskConfig);
                return 1;
            } else if (taskConfig != null && taskConfig.isEffective()) {
                // 如果任务配置存在且任务在有效期内，但是任务不在线，则重新启动它
                if (!online) {
                    start();
                    TComLogs.info("inspect task executor [{}], task conf is effective but not online restart it", taskConfig);
                    return 2;
                }
            }
        } catch (TaskInvokerException e) {
            if (this.inspectFailNum >= 3) {
                // 如果心跳已经失败了3次，再次尝试下心跳，如果还是异常，则直接将任务设置为已经停止
                try {
                    inspect();
                } catch (TaskInvokerException e1) {
                    return 1;
                }
            }
            throw new TaskInvokerException("inspect task [" + this + "] fail", e);
        }
        int resultCode = 0;
        switch (this.state) {
            case 0:
                resultCode = online ? 0 : 1;
                break;
            case 1:
                resultCode = online ? 2 : 1;
                break;
            case 2:
                resultCode = online ? 2 : 1;
                break;
            case 3:
                resultCode = online ? 2 : 1;
                break;
        }
        this.state = online ? 0 : 3;
        return resultCode;
    }

    public synchronized void destory() {
        if (this.reference != null) {
            this.reference.destroy();
            this.reference = null;
        }
        if (this.proxy != null) {
            this.proxy = null;
        }
        delete(getTaskId());
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public String getTaskId() {
        return this.taskId;
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
    protected String doStart(T config) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doStop(String localPrimarykey, T config) {
        throw new UnsupportedOperationException();
    }
}
