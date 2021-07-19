package com.github.mirs.banxiaoxiao.framework.dtask.control;

import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.control.proxy.TaskExecutorProxy;
import com.github.mirs.banxiaoxiao.framework.dtask.control.proxy.TaskInvokerException;
import com.github.mirs.banxiaoxiao.framework.dtask.control.proxy.dubbo.TaskExecutorProxyBuilder;
import com.github.mirs.banxiaoxiao.framework.dtask.seres.ServerResource;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zcy 2019年7月5日
 */
public class TaskExecutorProxyHolder<T extends TaskConfig> {

    private Map<String, Proxy<T>> taskProxys = new HashMap<String, Proxy<T>>();

    private TaskControlProperties taskControlProperties;

    private Class<?> taskExecutorProxyClass;

    public TaskExecutorProxyHolder(TaskControlProperties taskControlProperties) {
        super();
        this.taskControlProperties = taskControlProperties;
        this.taskExecutorProxyClass = taskControlProperties.getTaskExecutorProxyClass();
    }

    public void init() {
        Map<String, TaskExecutorProxy<T>> taskProxys = TaskExecutorProxyBuilder.create(taskControlProperties).getAllTaskExecutorProxy();
        if (taskProxys != null) {
            for (TaskExecutorProxy<T> proxy : taskProxys.values()) {
                proxy.init();
            }
        }
    }

    public void clean() {
        this.taskProxys.clear();
    }

    public synchronized List<Proxy<T>> getProxys() {
        return new ArrayList<Proxy<T>>(taskProxys.values());
    }

    public synchronized void removeTaskExecutorProxy(String taskId) {
        this.taskProxys.remove(taskId);
    }
    
    @SuppressWarnings("unchecked")
    public synchronized TaskExecutorProxy<T> createTaskExecutorProxy(T config, ServerResource serverResource) {
        TaskExecutorProxy<T> taskProxy = getTaskExecutorProxy(config.getTaskId());
        if (taskProxy != null) {
            return taskProxy;
        }
        if (context != null) {
            taskProxy = (TaskExecutorProxy<T>) context.getAutowireCapableBeanFactory().createBean(this.taskExecutorProxyClass);
        } else {
            try {
                taskProxy = (TaskExecutorProxy<T>) this.taskExecutorProxyClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new TaskInvokerException("create task proxy fail", e);
            }
        }
        taskProxy.setServerResource(serverResource);
        taskProxy.setTaskConfig(config);
        taskProxy.setTaskControlProperties(taskControlProperties);
        Proxy<T> proxy = new Proxy<T>();
        proxy.taskConfig = config;
        proxy.taskId = config.getTaskId();
        proxy.taskProxy = taskProxy;
        proxy.serverResource = serverResource;
        this.taskProxys.put(proxy.taskId, proxy);
        return taskProxy;
    }

    public TaskExecutorProxy<T> getTaskExecutorProxy(String taskId) {
        Proxy<T> proxy = this.taskProxys.get(taskId);
        return proxy == null ? null : proxy.taskProxy;
    }

    private ApplicationContext context;

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    static class Proxy<T extends TaskConfig> {

        TaskExecutorProxy<T> taskProxy;

        String taskId;

        ServerResource serverResource;

        T taskConfig;
    }
}
