package com.github.mirs.banxiaoxiao.framework.dtask.executor;

import com.github.mirs.banxiaoxiao.framework.common.util.JsonUtils;
import com.github.mirs.banxiaoxiao.framework.core.dcc.conf.BaseDConfig;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.dtask.Constants;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @param <T>
 * @author zcy 2019年5月31日
 */
public abstract class DccTaskExecutorEnv<T extends TaskConfig> extends BaseDConfig<DccTaskExecutorEnv<T>> {

    private T taskConfig;

    private String taskCode;

    private long lastUpdateTime;

    private String serverId;

    transient private Class<?> configModelClass;

    public DccTaskExecutorEnv(String taskCode) {
        this(null, taskCode);
    }

    public DccTaskExecutorEnv(T taskConfig, String taskCode) {
        super(String.format(Constants.KEY_FORMAT_TASKPROXY, taskCode));
        this.taskConfig = taskConfig;
        this.taskCode = taskCode;
        this.serverId = LocalServerIdHelper.getServerId();
        Class<?> slefClazz = getClass();
        while (slefClazz != Object.class) {
            Type t = slefClazz.getGenericSuperclass();
            if (t instanceof ParameterizedType) {
                Type[] args = ((ParameterizedType) t).getActualTypeArguments();
                if (args[0] instanceof Class) {
                    this.configModelClass = (Class<?>) args[0];
                    break;
                }
            }
            slefClazz = slefClazz.getSuperclass();
        }
    }

    public Class<?> getConfigModelClass() {
        return configModelClass;
    }

    public void setConfigModelClass(Class<?> configModel) {
        this.configModelClass = configModel;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public void setTaskConfig(T taskConfig) {
        this.taskConfig = taskConfig;
    }

    public void setTaskCode(String taskCode) {
        setAppKey(String.format(Constants.KEY_FORMAT_TASKPROXY, taskCode));
        this.taskCode = taskCode;
    }

    public String getTaskId() {
        return this.taskConfig.getTaskId();
    }

    public T getTaskConfig() {
        return this.taskConfig;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public String getServerId() {
        return serverId;
    }

    /**
     * 刷新任务数据到dcc
     */
    public void flushToDcc() {
        this.lastUpdateTime = System.currentTimeMillis();
        put(getTaskId(), this);
    }

    @SuppressWarnings({"hiding"})
    protected <T> T readData(String path) {
        byte[] data = null;
        try {
            data = getDccClient().getZk().getData(path, true, null);
        } catch (Exception e) {
            TComLogs.error("", e);
        }
        if (data == null) {
            return null;
        }
        String v = new String(data);
        return JsonUtils.fromJson(TypeToken.get(getClazz()).getType(), v);
    }

    /**
     * 从DCC中读取任务信息
     *
     * @return
     */
    public <A> A readFromDcc() {
        return readData(genChildPath(getTaskId()));
    }

    @Override
    public int hashCode() {
        return (getTaskCode() + getTaskConfig().getTaskId() + getServerId()).hashCode();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj) {
        if (!(obj instanceof DccTaskExecutorEnv)) {
            return false;
        }
        DccTaskExecutorEnv other = (DccTaskExecutorEnv) obj;
        return other.getTaskCode().equals(getTaskCode()) && other.getTaskId().equals(getTaskId()) && other.getServerId().equals(getServerId());
    }

    public String toString() {
        return getTaskCode() + " " + getTaskId() + " " + getServerId() + " " + " isEffective : "
                + (getTaskConfig() == null ? null : getTaskConfig().isEffective());
    }
}
