package com.github.mirs.banxiaoxiao.framework.rabbitmq.enable;

import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.boot.ModuleInitializer;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.batch.impl.DefaultBatchTransactionManager;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.consult.ConsultationDesk;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rabbit.config.RabbitRmqAutoConfiguration;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rpc.RmqInvoker;
import com.github.mirs.banxiaoxiao.framework.rabbitmq.rpc.impl.RmqInvokerImpl;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.annotation.Annotation;

/**
 * @author zcy 2019年6月19日
 */
public class RmqInitializer implements ModuleInitializer {

    @Override
    public void init(ConfigurableApplicationContext appContext) throws InitializeException {
    }

    @Override
    public void init(Annotation enableAnno, ConfigurableApplicationContext appContext) throws InitializeException {
        registerBean(RabbitRmqAutoConfiguration.class, appContext);
        registerBean(SubscribeAnnotationBeanFactory.class, appContext);
        registerBean(EventBusConfig.class, appContext);
        registerBean(ConsultationDesk.class, appContext);
        registerBean(DefaultBatchTransactionManager.class, appContext, RmqInvoker.class);
        registerBean(RmqInvokerImpl.class, appContext);
    }
}
