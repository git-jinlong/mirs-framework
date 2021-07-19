package com.github.mirs.banxiaoxiao.framework.dtask.control.defofmax;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.dtask.TaskException;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.control.BalanceAlgorithm;
import com.github.mirs.banxiaoxiao.framework.dtask.control.TaskControlProperties;
import com.github.mirs.banxiaoxiao.framework.dtask.control.TaskControlPropertiesAware;
import com.github.mirs.banxiaoxiao.framework.dtask.control.TaskStatis;
import com.github.mirs.banxiaoxiao.framework.dtask.seres.ServerResource;

import java.util.*;

public class MaxCapacityRemainBalanceAlgorithm<T extends TaskConfig> implements BalanceAlgorithm<T>, TaskControlPropertiesAware {

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
            throw new TaskException("MaxCapacityRemainBalanceAlgorithm does not init");
        }
    }

    @Override
    public ServerResource balancer(T taskConfig, TaskStatis statistics, boolean force) {
        if (statistics == null || statistics.getServerList() == null || statistics.getServerList().size() == 0) {
            throw new TaskException("no instance found");
        }
        checkInit();

        String existTargetServer = statistics.getTaskServer(taskConfig.getTaskId());
        if (StringUtil.isNotBlank(existTargetServer)) {//看看existTargetServer是否存在，如果不存在，则删除旧数据，重新分配
            ServerResource sr = statistics.getServer(existTargetServer);
            if (!force && sr != null) {
                TComLogs.info("task {} balancer server {} exist", taskConfig, sr);
                return sr;
            }
        }

        Map<String, Integer> serverCapacityMap = new HashMap<>();
        try {
            serverCapacityMap = this.capacityReader.getAllCapacity();
        } catch (TaskException e) {//读取异常时，设定默认值
            TComLogs.error("", e);
            for (ServerResource sr : statistics.getServerList()) {
                serverCapacityMap.put(sr.getId(), CapacityReader.DEFAULT_CAPACITY);
            }
        }
        String maxCapacityRestTargetServer = getMaxRest(statistics, serverCapacityMap);
        ServerResource targetSr = statistics.getServer(maxCapacityRestTargetServer);
        TComLogs.debug("task {} balancer server {}", taskConfig, targetSr);
        return targetSr;
    }

    /**
     * 根据 任务统计 和 服务器设定的能力值 计算获取剩余能力值最多的那台服务器
     **/
    private String getMaxRest(TaskStatis statistics, Map<String, Integer> serverCapacityMap) {
        if (serverCapacityMap == null || serverCapacityMap.size() == 0) {
            throw new TaskException("no server write capacity on dcc");
        }

        final Map<String, Integer> currentServerTaskCountMap = new HashMap<>();
        for (ServerResource sr : statistics.getServerList()) {
            int taskCount = statistics.getServerTasks(sr.getId()) == null ? 0 : statistics.getServerTasks(sr.getId()).size();
            currentServerTaskCountMap.put(sr.getId(), taskCount);
        }

        final Map<String, Integer> serverCapacityRestMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : serverCapacityMap.entrySet()) {
            String server = entry.getKey();
            int capacity = entry.getValue();
            int taskCount = currentServerTaskCountMap.get(server) == null ? 0 : currentServerTaskCountMap.get(server);
            serverCapacityRestMap.put(server, capacity - taskCount);
        }
        //降序
        List<Map.Entry<String, Integer>> serverDescendingList = new ArrayList<>(serverCapacityRestMap.entrySet());
        Collections.sort(serverDescendingList, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        if (serverDescendingList.get(0).getValue() <= 0) {
            throw new TaskException("balance task but have no available server,all server are full!");
        }
        return serverDescendingList.get(0).getKey();
    }


}
