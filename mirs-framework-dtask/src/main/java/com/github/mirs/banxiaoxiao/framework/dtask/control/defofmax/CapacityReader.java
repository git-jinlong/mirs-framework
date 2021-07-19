package com.github.mirs.banxiaoxiao.framework.dtask.control.defofmax;

import com.github.mirs.banxiaoxiao.framework.core.dcc.AbstractDccApp;
import com.github.mirs.banxiaoxiao.framework.dtask.TaskException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CapacityReader extends AbstractDccApp {

    private String taskCode;

    public static final int DEFAULT_CAPACITY = 45;//读取不到能力值时，默认返回此数字

    public CapacityReader(String taskCode) {
        super(String.format(CapacityRoot.PATH, taskCode));
        setClazz(Integer.class);
        setTaskCode(taskCode);
    }

    public int getCapacity(String serverID) {
        Integer capacity = readData(genChildPath(serverID));
        if (capacity == null || capacity < 0) {
            capacity = DEFAULT_CAPACITY;
        }
        return capacity;
    }

    public Map<String, Integer> getAllCapacity() {
        List<String> children = getDccClient().getChildren(getRoot());
        if (children == null || children.size() == 0) {
            throw new TaskException("can't find children of capacity");
        }
        Map<String, Integer> serverCapacityMap = new HashMap<>();
        for (String child : children) {
            Integer capacity = getCapacity(child);
            serverCapacityMap.put(child, capacity);
        }
        return serverCapacityMap;
    }

    public int getCapacitySum() {
        List<String> children = getDccClient().getChildren(getRoot());
        if (children == null || children.size() == 0) {
            throw new TaskException("can't find children of capacity");
        }
        int total = 0;
        for (String child : children) {
            total = total + getCapacity(child);
        }
        return total;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }
}
