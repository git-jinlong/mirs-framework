package com.github.mirs.banxiaoxiao.framework.dtask.control.def;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.control.AdjustAlgorithm;
import com.github.mirs.banxiaoxiao.framework.dtask.control.TaskStatis;
import com.github.mirs.banxiaoxiao.framework.dtask.seres.ServerResource;

import java.util.HashMap;
import java.util.Map;

/**
 * @param <T>
 * @author zcy 2019年5月30日
 */
public class MinUseAdjustAlgorithm<T extends TaskConfig> implements AdjustAlgorithm<T> {

    private float diffCoefficient = 0.3f;

    @Override
    public boolean needAdjust(T taskConfig, TaskStatis statistics) {
        Map<String, Integer> serverTaskNumMap = new HashMap<String, Integer>();
        for (ServerResource sr : statistics.getServerList()) {
            int num = statistics.getServerTasks(sr.getId()).size();
            serverTaskNumMap.put(sr.getId(), num);
        }
        int taskNum = statistics.getTaskServerMap().size();
        int serverNum = statistics.getServerList().size();
        int averageTaskNum = new Double(Math.ceil(taskNum / new Double(serverNum))).intValue();
        int maxTaskNum = averageTaskNum + new Double(Math.ceil((averageTaskNum * diffCoefficient))).intValue();
        String taskOnServerId = statistics.getTaskServer(taskConfig.getTaskId());
        if (taskOnServerId == null) {
            TComLogs.warn("adjust task [{}] but it was not found on the server.", taskConfig);
            return false;
        }
        int onServerTaskNum = serverTaskNumMap.get(taskOnServerId);
        TComLogs.debug("adjust task [{}], allServerNum={},allTaskNum={},averageTaskNum={},maxTaskNum={},onserver={},onServerTaskNum={}", taskConfig,
                serverNum, taskNum, averageTaskNum, maxTaskNum, taskOnServerId, onServerTaskNum);
        if (onServerTaskNum > maxTaskNum) {
            return true;
        } else {
            return false;
        }
    }
}
