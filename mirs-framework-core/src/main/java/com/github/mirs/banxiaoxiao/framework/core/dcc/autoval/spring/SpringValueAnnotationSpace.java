package com.github.mirs.banxiaoxiao.framework.core.dcc.autoval.spring;

import com.github.mirs.banxiaoxiao.framework.common.util.ClassUtil;
import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;
import com.github.mirs.banxiaoxiao.framework.core.dcc.autoval.AppDefaultSpace;
import com.github.mirs.banxiaoxiao.framework.core.dcc.autoval.Valistener;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.core.util.AopTargetUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.*;

/**
 * @author zcy 2018年9月14日
 */
public class SpringValueAnnotationSpace extends AppDefaultSpace implements Valistener, BeanPostProcessor {

    /**
     *
     */
    private static final long serialVersionUID = -6601666260758657310L;

    private Map<Class<?>, SpringBeanBinder> springBeanBinders = new HashMap<Class<?>, SpringBeanBinder>();

    private Map<String, List<Class<?>>> keyClassMap = new HashMap<String, List<Class<?>>>();

    public SpringValueAnnotationSpace(DccClient dccClient) {
        this(dccClient, "com.arcvideo");
    }

    public SpringValueAnnotationSpace(DccClient dccClient, String scanPackage) {
        super(dccClient);
        Set<Class<?>> clazzSet = ClassUtil.scanPackageMFCByAnnotation(scanPackage, true, Value.class);
        for (Class<?> clazz : clazzSet) {
            SpringBeanBinder springBeanBinder = new SpringBeanBinder(clazz);
            springBeanBinders.put(clazz, springBeanBinder);
            for (String key : springBeanBinder.getVals().keySet()) {
                List<Class<?>> clazzs = keyClassMap.get(key);
                if (clazzs == null) {
                    clazzs = new ArrayList<Class<?>>();
                    keyClassMap.put(key, clazzs);
                }
                if (!clazzs.contains(clazz)) {
                    clazzs.add(clazz);
                }
            }
        }
        registerListener(this);
    }

    public int getPriority() {
        return 400;
    }

    public static String namespace() {
        return BeeClientConfiguration.getLocalProperies().getAppName() + "_spring";
    }

    protected void initLocalVals() {
        for (SpringBeanBinder sbb : springBeanBinders.values()) {
            for (String key : sbb.getVals().keySet()) {
                // dcc里的配置优先级比spring bean默认值高。如果dcc中已经存在，则不覆盖
                if (!super.containsKey(key)) {
                    super.putNoNotice(key, sbb.getVals().get(key));
                }
            }
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        try {
            bean = AopTargetUtils.getTarget(bean);
        } catch (Exception e) {
        }
        Class<?> clazz = bean.getClass();
        SpringBeanBinder springBeanBinder = springBeanBinders.get(clazz);
        if (springBeanBinder != null) {
            springBeanBinder.bindSpringBean(bean);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void onChange(String namespace, String key, Object value) {
        List<Class<?>> clazzs = keyClassMap.get(key);
        if (clazzs != null) {
            for (Class<?> clazz : clazzs) {
                SpringBeanBinder springBeanBinder = springBeanBinders.get(clazz);
                if (springBeanBinder != null) {
                    try {
                        springBeanBinder.chanageVal(key, value);
                    } catch (Exception e) {
                        TComLogs.error("set val fail ", e);
                    }
                }
            }
        }
    }
}
