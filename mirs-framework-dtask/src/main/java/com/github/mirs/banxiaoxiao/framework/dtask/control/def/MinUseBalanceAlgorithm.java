package com.github.mirs.banxiaoxiao.framework.dtask.control.def;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.dtask.TaskException;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.control.BalanceAlgorithm;
import com.github.mirs.banxiaoxiao.framework.dtask.control.TaskStatis;
import com.github.mirs.banxiaoxiao.framework.dtask.seres.ServerResource;

import java.util.*;

/**
 * 服务器最少使用算法
 *
 * @param <T>
 * @author zcy 2019年5月30日
 */
public class MinUseBalanceAlgorithm<T extends TaskConfig> implements BalanceAlgorithm<T> {

    @Override
    public ServerResource balancer(T taskConfig, TaskStatis statistics, boolean force) {
        if (statistics.getServerList() == null || statistics.getServerList().size() == 0) {
            throw new TaskException("no instance found");
        }
        String existTargetServer = statistics.getTaskServer(taskConfig.getTaskId());
        if (StringUtil.isNotBlank(existTargetServer)) {
            // 看看instance 是否存在，如果不存在了，则删除旧数据，重新分配
            ServerResource sr = statistics.getServer(existTargetServer);
            if (!force && sr != null) {
                TComLogs.info("task {} balancer server {} exist", taskConfig, sr);
                return sr;
            }
        }
        Map<String, Integer> serverTaskNumMap = new HashMap<String, Integer>();
        for (ServerResource sr : statistics.getServerList()) {
            int num = statistics.getServerTasks(sr.getId()).size();
            serverTaskNumMap.put(sr.getId(), num);
        }
        List<String> serverList = new ArrayList<String>(serverTaskNumMap.keySet());
        Collections.sort(serverList, new Comparator<String>() {

            public int compare(String arg0, String arg1) {
                int int0 = serverTaskNumMap.get(arg0);
                int int1 = serverTaskNumMap.get(arg1);
                return int0 - int1;
            }
        });
        String minTaskNumTargetServer = serverList.get(0);
        ServerResource targetSr = statistics.getServer(minTaskNumTargetServer);
        TComLogs.info("task {} balancer server {}", taskConfig, targetSr);
        return targetSr;
    }
}
