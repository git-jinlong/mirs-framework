package com.github.mirs.banxiaoxiao.framework.dtask.control;

import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;

import java.util.List;

/**
 * @author zcy 2019年4月25日
 */
public class TaskControlProperties {

    /**
     * 任务编码，每一类型任务的任务编码确保唯一性。如果摄像机解码任务、抓拍机接入任务等
     */
    private String taskCode;

    /**
     * 任务配置模型真实的class
     */
    private Class<? extends TaskConfig> configModelClass;

    /**
     * 任务负载算法
     */
    private Class<?> balanceAlgorithmClass;

    /**
     * 任务調整算法
     */
    private Class<?> adjustAlgorithmClass;

    private Class<?> taskExecutorProxyClass;

    /**
     * 单位毫秒
     */
    private int period = 10000;

    /**
     * 该类任务可以在哪些服务器上运行
     */
    private List<String> canRunApps;

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public Class<? extends TaskConfig> getConfigModelClass() {
        return configModelClass;
    }

    public void setConfigModelClass(Class<? extends TaskConfig> configModelClass) {
        this.configModelClass = configModelClass;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String toString() {
        return this.taskCode + " " + this.configModelClass;
    }

    public void setBalanceAlgorithmClass(Class<?> balanceAlgorithmClass) {
        this.balanceAlgorithmClass = balanceAlgorithmClass;
    }

    public void setAdjustAlgorithmClass(Class<?> adjustAlgorithmClass) {
        this.adjustAlgorithmClass = adjustAlgorithmClass;
    }

    public Class<?> getBalanceAlgorithmClass() {
        return balanceAlgorithmClass;
    }

    public Class<?> getAdjustAlgorithmClass() {
        return adjustAlgorithmClass;
    }

    public Class<?> getTaskExecutorProxyClass() {
        return taskExecutorProxyClass;
    }

    public void setTaskExecutorProxyClass(Class<?> taskExecutorProxyClass) {
        this.taskExecutorProxyClass = taskExecutorProxyClass;
    }

    public List<String> getCanRunApps() {
        return canRunApps;
    }

    public void setCanRunApps(List<String> canRunApps) {
        this.canRunApps = canRunApps;
    }
}
