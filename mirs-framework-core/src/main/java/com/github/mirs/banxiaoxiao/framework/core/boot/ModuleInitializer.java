package com.github.mirs.banxiaoxiao.framework.core.boot;

import java.lang.annotation.Annotation;

import org.springframework.context.ConfigurableApplicationContext;


/**
 * 模块初始化执行器，在ApplicationInitializer初始化器上增加可传递开启注解的方法，如果实现该接口，
 * 则优先调用 {@link #init(Annotation, ConfigurableApplicationContext)},然后调用 {@link #init(ConfigurableApplicationContext)}
 * @author zcy 2019年6月3日
 */
public interface ModuleInitializer <T extends Annotation> extends ApplicationInitializer {
    
    /**
     * @param enableAnno 显示开启的组件注解实例，如果不是以显示注解的方式开启组件不会调用该方法
     * @param appContext
     * @throws InitializeException
     */
    public void init(T enableAnno, ConfigurableApplicationContext appContext) throws InitializeException;
}
