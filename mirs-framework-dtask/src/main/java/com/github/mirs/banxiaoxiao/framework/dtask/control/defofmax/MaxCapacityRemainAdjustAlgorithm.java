package com.github.mirs.banxiaoxiao.framework.dtask.control.defofmax;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.dtask.TaskException;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.control.AdjustAlgorithm;
import com.github.mirs.banxiaoxiao.framework.dtask.control.TaskControlProperties;
import com.github.mirs.banxiaoxiao.framework.dtask.control.TaskControlPropertiesAware;
import com.github.mirs.banxiaoxiao.framework.dtask.control.TaskStatis;
import com.github.mirs.banxiaoxiao.framework.dtask.seres.ServerResource;

import java.util.HashMap;
import java.util.Map;


public class MaxCapacityRemainAdjustAlgorithm<T extends TaskConfig> implements AdjustAlgorithm<T>, TaskControlPropertiesAware {

    private TaskControlProperties properties;

    private CapacityReader capacityReader;

    @Override
    public void setTaskControlProperties(TaskControlProperties properties) {
        this.properties = properties;
        init();
    }

    private void init() {
        this.capacityReader = new CapacityReader(this.properties.getTaskCode());
    }

    private void checkInit() {
        if (this.capacityReader == null) {
            throw new TaskException("MaxCapacityRemainAdjustAlgorithm does not init");
        }
    }

    @Override
    public boolean needAdjust(T taskConfig, TaskStatis statistics) {
        if (statistics == null || statistics.getServerList() == null || statistics.getServerList().size() == 0) {
            throw new TaskException("no instance found");
        }
        checkInit();

        String taskOnServerId = statistics.getTaskServer(taskConfig.getTaskId());
        if (taskOnServerId == null) {
            TComLogs.warn("adjust task [{}], but it was not found on the server.", taskConfig);
            return false;
        }

        int taskSum = statistics.getTaskServerMap().size();

        int capacitySum;
        int capacityOnServer;
        try {
            capacitySum = this.capacityReader.getCapacitySum();
            capacityOnServer = this.capacityReader.getCapacity(taskOnServerId);
        } catch (TaskException e) {//读取异常时，设定默认值
            capacitySum = CapacityReader.DEFAULT_CAPACITY * statistics.getServerList().size();
            capacityOnServer = CapacityReader.DEFAULT_CAPACITY;
        }

        if (taskSum >= capacitySum) {
            TComLogs.warn("adjust task [{}], but capacity <= task total, all server are full!", taskConfig);
            return false;
        }

        final Map<String, Integer> currentServerTaskCountMap = new HashMap<>();
        for (ServerResource sr : statistics.getServerList()) {
            int taskCount = statistics.getServerTasks(sr.getId()) == null ? 0 : statistics.getServerTasks(sr.getId()).size();
            currentServerTaskCountMap.put(sr.getId(), taskCount);
        }

        int averageRestCapacity = new Double(Math.floor((capacitySum - taskSum) / new Double(statistics.getServerList().size()))).intValue();//剩余平均值
        int taskCountOnServer = currentServerTaskCountMap.get(taskOnServerId) == null ? 0 : currentServerTaskCountMap.get(taskOnServerId);
        int restCapacityOnServer = capacityOnServer - taskCountOnServer;//剩余值
        TComLogs.debug("adjust task [{}], oldServer={}, task count={}, capacity={}, rest={}, rest average={}", taskConfig, taskOnServerId, taskCountOnServer, capacityOnServer, restCapacityOnServer, averageRestCapacity);
        if (restCapacityOnServer < 0 || (taskCountOnServer > 0 && restCapacityOnServer < averageRestCapacity)) {
            return true;
        }
        return false;
    }
}
