package com.github.mirs.banxiaoxiao.framework.dtask.control;

import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;

/**
 * 任务调整算法
 * 
 * @author zcy 2019年4月29日
 */
public interface AdjustAlgorithm<T extends TaskConfig> {

    /**
     * 是否需要调整任务，如果需要调整，则会由control 先停止该任务，然后调用balance算法分配到新的服务器上运行
     * 
     * @param taskConfig
     * @param taskStatis
     * @return
     */
    public boolean needAdjust(T taskConfig, TaskStatis taskStatis);
}
