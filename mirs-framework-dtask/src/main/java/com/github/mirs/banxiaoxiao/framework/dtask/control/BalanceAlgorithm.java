package com.github.mirs.banxiaoxiao.framework.dtask.control;

import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.seres.ServerResource;

/**
 * @author zcy 2019年3月18日
 */
public interface BalanceAlgorithm<T extends TaskConfig> {

    /**
     * 负载均衡计算，计算task分配到哪个client上运行
     * 
     * @param taskConfig
     * @param statis
     * @param force
     * @return
     * @throws NotEnoughSerResException
     *             没有足够的运行资源抛出该异常
     */
    public ServerResource balancer(T taskConfig, TaskStatis statis, boolean force) ;
}
