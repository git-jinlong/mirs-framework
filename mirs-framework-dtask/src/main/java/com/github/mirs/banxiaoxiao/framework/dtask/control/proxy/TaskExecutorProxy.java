package com.github.mirs.banxiaoxiao.framework.dtask.control.proxy;

import com.github.mirs.banxiaoxiao.framework.dtask.TaskException;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.control.TaskControlProperties;
import com.github.mirs.banxiaoxiao.framework.dtask.seres.ServerResource;

/**
 * @author zcy 2019年7月5日
 * @param <T>
 */
public interface TaskExecutorProxy<T extends TaskConfig> {

    public static final int INSPECT_RUNNING = 0;

    public static final int INSPECT_STOPED = 1;

    public static final int INSPECT_RETRY_START = 2;

    public static final int INSPECT_RETRY_STOP = 3;

    public void setServerResource(ServerResource serverResource);

    public void setTaskConfig(T taskConfig);

    public void setTaskControlProperties(TaskControlProperties dtaskProperties);

    /**
     * 初始化代理，在执行初始化方法前， {@code #setServerResource(ServerResource)}、{@code #setTaskConfig(T)}、{@code #setTaskControlProperties(TaskControlProperties)}
     * 已经执行过了
     */
    public void init();

    /**
     * 消耗代理，和 stop的区别是destory后整个对象会被gc，而stop表示当前任务暂停但代理对象不会被销毁后续还有可能在start
     */
    public void destory();

    /**
     * <pre>
     * 任务巡检，如果当前任务不正常，则自动调整状态并返回相应正确的状态
     * 0 : 正常运行 {@link #INSPECT_RUNNING}
     * 1 : 已经停止或不存在  {@link #INSPECT_STOPED}
     * 2 : 尝试启动了但是失败，尝试重启  {@link #INSPECT_RETRY_START}
     * 3 : 尝试停止了但是失败，尝试重新停止 {@link #INSPECT_RETRY_STOP}
     * </pre>
     * 
     * @param config
     * @return
     * @throws TaskException
     *             有任何问题请抛异常
     */
    public int inspect(T config);

    public void start();

    public void stop();

    public boolean isRunning();

    public boolean isStoped();
}
