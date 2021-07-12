package com.github.mirs.banxiaoxiao.framework.core.drpc.p2p;

import static org.apache.dubbo.config.spring.util.ObjectUtils.of;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import org.apache.dubbo.config.spring.context.annotation.DubboClassPathBeanDefinitionScanner;
import com.github.mirs.banxiaoxiao.framework.core.dcc.client.ClientId;
import com.github.mirs.banxiaoxiao.framework.core.drpc.annotation.P2P;
import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.AnnotationPropertyValuesAdapter;
import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.DServiceBeanFactory;

public class P2PServiceBeanFactory extends DServiceBeanFactory {

    /** */
    private static final long serialVersionUID = -3874017485777633794L;

    public P2PServiceBeanFactory(String... packagesToScan) {
        super(packagesToScan);
    }

    @Override
    protected void addIncludeFilter(ClassPathBeanDefinitionScanner scanner) {
        scanner.addIncludeFilter(new AnnotationTypeFilter(P2P.class));
    }

    @Override
    protected void registerServiceBean(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry registry,
            DubboClassPathBeanDefinitionScanner scanner) {
        Class<?> beanClass = resolveClass(beanDefinitionHolder);
        P2P service = findAnnotation(beanClass, P2P.class);
        Class<?> interfaceClass = resolveServiceInterfaceClass(beanClass, service);
        String annotatedServiceBeanName = beanDefinitionHolder.getBeanName();
        AbstractBeanDefinition serviceBeanDefinition = buildServiceBeanDefinition(service, interfaceClass, annotatedServiceBeanName);
        // ServiceBean Bean name
        String beanName = generateServiceBeanName(service, interfaceClass, annotatedServiceBeanName);
        if (scanner.checkCandidate(beanName, serviceBeanDefinition)) { // check duplicated candidate bean
            registry.registerBeanDefinition(beanName, serviceBeanDefinition);
            if (logger.isInfoEnabled()) {
                logger.warn("The BeanDefinition[" + serviceBeanDefinition + "] of ServiceBean has been registered with name : " + beanName);
            }
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("The Duplicated BeanDefinition[" + serviceBeanDefinition + "] of ServiceBean[ bean name : " + beanName
                        + "] was be found , Did @DubboComponentScan scan to same package in many times?");
            }
        }
    }

    private AbstractBeanDefinition buildServiceBeanDefinition(P2P service, Class<?> interfaceClass, String annotatedServiceBeanName) {
        BeanDefinitionBuilder builder = rootBeanDefinition(P2PServiceBean.class);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
        String[] ignoreAttributeNames = of("provider", "monitor", "application", "module", "registry", "protocol", "interface");
        propertyValues.addPropertyValues(new AnnotationPropertyValuesAdapter(service, getEnvironment(), ignoreAttributeNames));
        // References "ref" property to annotated-@Service Bean
        addPropertyReference(builder, "ref", annotatedServiceBeanName);
        // Set interface
        builder.addPropertyValue("interface", interfaceClass.getName());
        return builder.getBeanDefinition();
    }

    private String generateServiceBeanName(P2P service, Class<?> interfaceClass, String annotatedServiceBeanName) {
        StringBuilder beanNameBuilder = new StringBuilder(P2PServiceBean.class.getSimpleName());
        beanNameBuilder.append(SEPARATOR).append(annotatedServiceBeanName);
        String interfaceClassName = interfaceClass.getName();
        beanNameBuilder.append(SEPARATOR).append(interfaceClassName);
        String group = getGroup();
        if (StringUtils.hasText(group)) {
            beanNameBuilder.append(SEPARATOR).append(group);
        }
        return beanNameBuilder.toString();
    }

    public String getGroup() {
        return ClientId.get();
    }

    private Class<?> resolveServiceInterfaceClass(Class<?> annotatedServiceBeanClass, P2P service) {
        Class<?> interfaceClass = service.interfaceClass();
        if (void.class.equals(interfaceClass)) {
            interfaceClass = null;
        }
        if (interfaceClass == null) {
            Class<?>[] allInterfaces = annotatedServiceBeanClass.getInterfaces();
            if (allInterfaces.length > 0) {
                interfaceClass = allInterfaces[0];
            }
        }
        Assert.notNull(interfaceClass, "@Service interfaceClass() or interfaceName() or interface class must be present!");
        Assert.isTrue(interfaceClass.isInterface(), "The type that was annotated @Service is not an interface!");
        return interfaceClass;
    }
}
