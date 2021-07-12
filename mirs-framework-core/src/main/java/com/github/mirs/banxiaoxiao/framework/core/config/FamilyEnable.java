package com.github.mirs.banxiaoxiao.framework.core.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mirs.banxiaoxiao.framework.core.dcc.annotation.DccClientEnable;
import com.github.mirs.banxiaoxiao.framework.core.dcc.autoval.annotation.AutovalEnable;
import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.annotation.DrpcEnable;
import com.github.mirs.banxiaoxiao.framework.core.event.enable.EventListenerEnable;
import com.github.mirs.banxiaoxiao.framework.core.license.enable.LicenseEnable;
import com.github.mirs.banxiaoxiao.framework.core.log.enable.LogEnable;
import com.github.mirs.banxiaoxiao.framework.core.monitor.annotation.MonitorEnable;
import com.github.mirs.banxiaoxiao.framework.core.woodpecker.enable.WoodpeckerEnable;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@DrpcEnable
@DccClientEnable
@AutovalEnable
@MonitorEnable
@WoodpeckerEnable
@LicenseEnable
@EventListenerEnable
@LogEnable
public @interface FamilyEnable {
}
