package com.github.mirs.banxiaoxiao.framework.dtask.conf;

import java.util.List;

/**
 * <pre>
 * 任务配置提供者，业务层通过该配置管理器管理控制任务的启停
 * 当 {@link TaskConfig#isEffective()} 返回false，或者config不存在时，dtask开始自动执行停止任务流程
 * </pre>
 * 
 * @author zcy 2019年4月25日
 */
public interface TaskConfigProvider<T extends TaskConfig> {

    default void setTaskCode(String taskCode) {
        //
    }

    /**
     * 是否存在任务配置
     * 
     * @param taskId
     * @return
     */
    public boolean isExist(String taskId);

    /**
     * @param taskId
     * @return
     */
    public T getTaskConfig(String taskId);

    /**
     * 分页获取任务id，分页取值从0开始，如果返回null对象或者size=0的list，则表示全部获取完了
     * 
     * @param pageNum
     * @return
     */
    public List<T> getTaskConfigs(int pageNum);
}
