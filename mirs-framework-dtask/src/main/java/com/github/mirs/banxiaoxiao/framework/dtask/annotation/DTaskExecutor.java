package com.github.mirs.banxiaoxiao.framework.dtask.annotation;

import com.github.mirs.banxiaoxiao.framework.core.dcc.annotation.DccClientEnable;
import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.annotation.DrpcEnable;
import com.github.mirs.banxiaoxiao.framework.dtask.annotation.enable.DTaskEnable;
import com.github.mirs.banxiaoxiao.framework.dtask.executor.TaskExecutor;

import java.lang.annotation.*;

/**
 * @author zcy 2019年5月29日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DccClientEnable
@DrpcEnable
@DTaskEnable
public @interface DTaskExecutor {

    /**
     * 任务类别
     *
     * @return
     */
    String taskCode();

    /**
     * worker端执行任务的执行器
     *
     * @return
     */
    Class<? extends TaskExecutor<?>> executor();
}
