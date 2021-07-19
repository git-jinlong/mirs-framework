package com.github.mirs.banxiaoxiao.framework.dtask.control;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.dtask.seres.ServerResource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务统计信息，每个任务跑着哪个服务器上，哪些服务器上跑了哪些任务
 *
 * @author zcy 2019年4月29日
 */
public class TaskStatis implements Serializable {

    private static final long serialVersionUID = 712953254888016539L;

    private Map<String, String> taskServerMap = new HashMap<String, String>();

    private List<ServerResource> serverList = new ArrayList<ServerResource>();

    public Map<String, String> getTaskServerMap() {
        return taskServerMap;
    }

    public void setTaskServerMap(Map<String, String> taskServerMap) {
        this.taskServerMap = taskServerMap;
    }

    public void putTaskServer(String taskId, String serverId) {
        getTaskServerMap().put(taskId, serverId);
    }

    /**
     * 获取任务在哪台服务器上运行
     *
     * @param taskId
     * @return
     */
    public String getTaskServer(String taskId) {
        return this.taskServerMap.get(taskId);
    }

    /**
     * 获取目标服务器上跑了哪些任务
     *
     * @param serverId
     * @return
     */
    public List<String> getServerTasks(String serverId) {
        List<String> taskList = new ArrayList<String>();
        this.taskServerMap.forEach((taskId, serId) -> {
            if (StringUtil.equals(serId, serverId)) {
                taskList.add(taskId);
            }
        });
        return taskList;
    }

    /**
     * 获取可运行服务器列表
     *
     * @return
     */
    public List<ServerResource> getServerList() {
        return serverList;
    }

    public ServerResource getServer(String serverId) {
        for (ServerResource sr : serverList) {
            if (sr.getId().equals(serverId)) {
                return sr;
            }
        }
        return null;
    }

    public void setServerList(List<ServerResource> serverList) {
        this.serverList = serverList;
    }
}
