package com.github.mirs.banxiaoxiao.framework.dtask.annotation.enable;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.boot.ModuleInitializer;
import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.dtask.annotation.DTaskControl;
import com.github.mirs.banxiaoxiao.framework.dtask.annotation.DTaskExecutor;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.dcc.DccConfigManager;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.mix.MixTaskConfigProvider;
import com.github.mirs.banxiaoxiao.framework.dtask.control.TaskControl;
import com.github.mirs.banxiaoxiao.framework.dtask.control.TaskControlProperties;
import com.github.mirs.banxiaoxiao.framework.dtask.executor.LocalServerIdHelper;
import com.github.mirs.banxiaoxiao.framework.dtask.executor.wrapper.TaskExecutorDesk;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

/**
 * @author zcy 2019年5月29日
 */
public class DTaskInitializer implements ModuleInitializer {

    @Override
    public void init(Annotation enableAnno, ConfigurableApplicationContext appContext) throws InitializeException {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(ConfigurableApplicationContext appContext) throws InitializeException {
        Class<?> applicationClass = BeeClientConfiguration.getLocalProperies().getApplicationClasss();
        List<DTaskControl> dTaskLeaderAnnotations = new ArrayList<DTaskControl>();
        List<DTaskExecutor> dTaskWorkerAnnotations = new ArrayList<DTaskExecutor>();
        getDtaskAnnot(applicationClass, dTaskLeaderAnnotations, dTaskWorkerAnnotations);
        for (DTaskControl leader : dTaskLeaderAnnotations) {
            registerDTaskLeader(leader, appContext);
        }
        for (DTaskExecutor worker : dTaskWorkerAnnotations) {
            registerDTaskWorker(worker, appContext);
        }
    }

    private void registerDTaskLeader(DTaskControl worker, ConfigurableApplicationContext appContext) {
        String taskCode = worker.taskCode();
        Class<? extends TaskConfig> configModelClass = worker.configModelClass();
        Class<?> balanceAlgorithmClass = worker.balanceAlgorithm();
        Class<?> adjustAlgorithmClass = worker.adjustAlgorithm();
        int period = worker.period();
        String[] canRunApps = worker.canRunApps();
        TaskControlProperties dtaskConfig = new TaskControlProperties();
        dtaskConfig.setTaskCode(taskCode);
        dtaskConfig.setConfigModelClass(configModelClass);
        dtaskConfig.setBalanceAlgorithmClass(balanceAlgorithmClass);
        dtaskConfig.setAdjustAlgorithmClass(adjustAlgorithmClass);
        dtaskConfig.setTaskExecutorProxyClass(worker.taskExecutorProxy());
        dtaskConfig.setPeriod(period);
        List<String> canRunAppList = new ArrayList<String>();
        if (canRunApps != null) {
            for (String canRunApp : canRunApps) {
                canRunAppList.add(canRunApp);
            }
        }
        dtaskConfig.setCanRunApps(canRunAppList);
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) appContext;
        String serverResourceDiscoveryBeanName = createServerResourceDiscovery(worker, appContext, canRunAppList);
        String taskConfigProviderBeanName = createTaskConfigProvider(worker, appContext);
        String adjustAlgorithmName = registerBean(dtaskConfig.getAdjustAlgorithmClass(), appContext);
        String balanceAlgorithmName = registerBean(dtaskConfig.getBalanceAlgorithmClass(), appContext);
        BeanDefinitionBuilder builder = rootBeanDefinition(TaskControl.class);
        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        builder.addConstructorArgValue(dtaskConfig);
        builder.setInitMethodName("start");
        builder.addPropertyReference("taskConfigProvider", taskConfigProviderBeanName);
        builder.addPropertyReference("serverResourceDiscovery", serverResourceDiscoveryBeanName);
        builder.addPropertyReference("adjust", adjustAlgorithmName);
        builder.addPropertyReference("balancer", balanceAlgorithmName);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        String controlBeanName = beanDefinition.getBeanClassName() + "_" + taskCode;
        registry.registerBeanDefinition(controlBeanName, beanDefinition);
    }

    private String createServerResourceDiscovery(DTaskControl worker, ConfigurableApplicationContext appContext, List<String> canRunAppList) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) appContext;
        BeanDefinitionBuilder builder = rootBeanDefinition(worker.serverResourceDiscovery());
        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        builder.addPropertyValue("appnames", canRunAppList);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        String serverResourceDiscoveryBeanName = beanDefinition.getBeanClassName() + "_" + worker.taskCode();
        registry.registerBeanDefinition(serverResourceDiscoveryBeanName, beanDefinition);
        return serverResourceDiscoveryBeanName;
    }

    private String createTaskConfigProvider(DTaskControl worker, ConfigurableApplicationContext appContext) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) appContext;
        BeanDefinitionBuilder builder = rootBeanDefinition(worker.taskConfigProvider());
        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        builder.addPropertyValue("taskCode", worker.taskCode());
        if (worker.taskConfigProvider().equals(DccConfigManager.class)) {
            builder.addPropertyValue("clazz", worker.configModelClass());
        }
        if (worker.taskConfigProvider().equals(MixTaskConfigProvider.class)) {
            //dccConfig
            BeanDefinitionBuilder dccTaskConfigBuilder = rootBeanDefinition(worker.mixDccTaskConfigProvider());
            dccTaskConfigBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
            dccTaskConfigBuilder.addPropertyValue("clazz", worker.configModelClass());
            AbstractBeanDefinition dccTaskConfigBeanDefinition = dccTaskConfigBuilder.getBeanDefinition();
            String dccTaskConfigBeanName = dccTaskConfigBeanDefinition.getBeanClassName() + "_" + worker.taskCode();
            registry.registerBeanDefinition(dccTaskConfigBeanName, dccTaskConfigBeanDefinition);

            //mysqlAdapterConfig
            builder.addConstructorArgReference(dccTaskConfigBeanName);
            builder.addConstructorArgReference(worker.mixSqlDelegateBeanName());
        }
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        String taskConfigProviderBeanName = beanDefinition.getBeanClassName() + "_" + worker.taskCode();
        registry.registerBeanDefinition(taskConfigProviderBeanName, beanDefinition);
        return taskConfigProviderBeanName;
    }

    private void registerDTaskWorker(DTaskExecutor worker, ConfigurableApplicationContext appContext) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) appContext;
        String taskCode = worker.taskCode();
        Class<?> executorClass = worker.executor();
        BeanDefinitionBuilder builder = rootBeanDefinition(TaskExecutorDesk.class);
        builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        builder.setLazyInit(true);
        builder.addConstructorArgValue(executorClass);
        builder.addConstructorArgValue(taskCode);
        builder.addConstructorArgValue(LocalServerIdHelper.getServerId());
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        String deskBeanName = executorClass.getName() + "_desk";
        registry.registerBeanDefinition(deskBeanName, beanDefinition);
    }

    private void getDtaskAnnot(Class<?> clazz, List<DTaskControl> dTaskLeaderAnnotations, List<DTaskExecutor> dTaskWorkerAnnotations) {
        if (clazz == null || clazz.equals(Object.class) || StringUtil.startsWith(clazz.getName(), "java")) {
            return;
        }
        for (Annotation annotation : clazz.getAnnotations()) {
            Class<?> annClass = annotation.annotationType();
            if (annClass.equals(DTaskControl.class)) {
                dTaskLeaderAnnotations.add((DTaskControl) annotation);
            } else if (annClass.equals(DTaskExecutor.class)) {
                dTaskWorkerAnnotations.add((DTaskExecutor) annotation);
            }
            if (!annClass.equals(clazz)) {
                getDtaskAnnot(annClass, dTaskLeaderAnnotations, dTaskWorkerAnnotations);
            }
        }
        getDtaskAnnot(clazz.getSuperclass(), dTaskLeaderAnnotations, dTaskWorkerAnnotations);
        for (Class<?> intf : clazz.getInterfaces()) {
            getDtaskAnnot(intf, dTaskLeaderAnnotations, dTaskWorkerAnnotations);
        }
    }

}
