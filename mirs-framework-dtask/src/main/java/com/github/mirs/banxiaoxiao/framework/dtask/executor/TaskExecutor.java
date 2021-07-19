package com.github.mirs.banxiaoxiao.framework.dtask.executor;

import com.github.mirs.banxiaoxiao.framework.dtask.TaskException;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;

/**
 * 任务执行器，该执行器通常运行在客户端
 * 
 * @author zcy 2019年3月18日
 */
public interface TaskExecutor<T extends TaskConfig> {

    public static final int INSPECT_RUNNING = 0;

    public static final int INSPECT_STOPED = 1;

    public static final int INSPECT_RETRY_START = 2;

    public static final int INSPECT_RETRY_STOP = 3;

    /**
     * @param config
     * @throws TaskException
     *             有任何问题请抛异常
     */
    public void start(T config) throws TaskException;

    /**
     * @param config
     * @throws TaskException
     *             有任何问题请抛异常
     */
    public void stop(T config) throws TaskException;

    
    /**
     * 当服务端和client失联后，发现任务已经运行在其他服务器上，强制kill本地任务
     * @param config
     * @throws TaskException
     */
    public void kill(T config) throws TaskException;
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
    public int inspect(T config) throws TaskException;
}
