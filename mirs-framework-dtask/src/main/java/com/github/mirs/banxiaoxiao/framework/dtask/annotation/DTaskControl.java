package com.github.mirs.banxiaoxiao.framework.dtask.annotation;

import com.github.mirs.banxiaoxiao.framework.core.dcc.annotation.DccClientEnable;
import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.annotation.DrpcEnable;
import com.github.mirs.banxiaoxiao.framework.dtask.annotation.enable.DTaskEnable;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.dcc.DccConfigManager;
import com.github.mirs.banxiaoxiao.framework.dtask.control.def.MinUseAdjustAlgorithm;
import com.github.mirs.banxiaoxiao.framework.dtask.control.def.MinUseBalanceAlgorithm;
import com.github.mirs.banxiaoxiao.framework.dtask.control.proxy.dubbo.DubboTaskExecutorProxy;
import com.github.mirs.banxiaoxiao.framework.dtask.seres.DccServerResourceDiscovery;

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
public @interface DTaskControl {

    String taskCode();

    Class<? extends TaskConfig> configModelClass();

    Class<?> balanceAlgorithm() default MinUseBalanceAlgorithm.class;

    Class<?> adjustAlgorithm() default MinUseAdjustAlgorithm.class;

    Class<?> serverResourceDiscovery() default DccServerResourceDiscovery.class;

    Class<?> taskConfigProvider() default DccConfigManager.class;

    Class<?> taskExecutorProxy() default DubboTaskExecutorProxy.class;

    Class<?> mixDccTaskConfigProvider() default DccConfigManager.class;

    String mixSqlDelegateBeanName() default "";

    int period() default 10000;

    String[] canRunApps();
}
