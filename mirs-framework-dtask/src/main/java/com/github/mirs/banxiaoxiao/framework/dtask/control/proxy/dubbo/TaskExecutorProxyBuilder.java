package com.github.mirs.banxiaoxiao.framework.dtask.control.proxy.dubbo;

import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.control.TaskControlProperties;
import com.github.mirs.banxiaoxiao.framework.dtask.control.proxy.TaskExecutorProxy;
import com.github.mirs.banxiaoxiao.framework.dtask.executor.DccTaskExecutorEnv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zcy 2019年5月30日
 */
@SuppressWarnings("rawtypes")
public class TaskExecutorProxyBuilder extends DccTaskExecutorEnv {
    private TaskControlProperties dtaskConfig;
    
    @SuppressWarnings("unchecked")
    private TaskExecutorProxyBuilder(TaskControlProperties dtaskConfig) {
        super(dtaskConfig.getTaskCode());
        this.dtaskConfig = dtaskConfig;
        setClazz(dtaskConfig.getTaskExecutorProxyClass());
        setConfigModelClass(dtaskConfig.getConfigModelClass());
    }

    public static TaskExecutorProxyBuilder create(TaskControlProperties dtaskConfig) {
        return new TaskExecutorProxyBuilder(dtaskConfig);
    }

    @SuppressWarnings("unchecked")
    public <T extends TaskConfig> TaskExecutorProxy<T> getTaskExecutorProxy(T config) {
        TaskExecutorProxy<T> proxy = (TaskExecutorProxy<T>) super.find(config.getTaskId());
        if (proxy != null) {
            proxy.setTaskConfig(config);
        }
        return proxy;
    }

    @SuppressWarnings("unchecked")
    public <T extends TaskConfig> Map<String, TaskExecutorProxy<T>> getAllTaskExecutorProxy() {
        Map<String, TaskExecutorProxy<T>> proxys = new HashMap<String, TaskExecutorProxy<T>>();
        List<String> taskProxyIdList = super.all();
        for (String taskProxyId : taskProxyIdList) {
            TaskExecutorProxy<T> proxy = (TaskExecutorProxy<T>) super.find(taskProxyId);
            if (proxy != null) {
                proxy.setTaskControlProperties(dtaskConfig);
                proxys.put(taskProxyId, proxy);
            }
        }
        return proxys;
    }
}
