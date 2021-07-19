package com.github.mirs.banxiaoxiao.framework.dtask.control;

import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;

public interface TaskStarter<T extends TaskConfig> {

    boolean startTask(T taskConfig);//成功开启任务 返回true；失败false

}