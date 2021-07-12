package com.github.mirs.banxiaoxiao.framework.core.boot;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.DReferenceBean;
import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.DServiceBean;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * boot 初始化执行接口，在应用启动生命周期中首先被执行。
 * <p>
 * {@link #order()}方法用于标识该执行器的优先级，数字越小优先级越高， 默认取值 ApplicationInitializer.COMMON_ORDER : {@value #COMMON_ORDER}
 *
 * @author zcy 2019年4月15日
 */
public interface ApplicationInitializer {

    public static int COMMON_ORDER = 100;

    public static int HIGH_ORDER = 1;

    public static int LOW_ORDER = 200;

    public static BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

    public void init(ConfigurableApplicationContext appContext) throws InitializeException;

    default public int order() {
        return COMMON_ORDER;
    }

    default public String registerBean(Class<?> clazz, ConfigurableApplicationContext appContext) {
        return registerBean(clazz, appContext, new AnnotationBeanNameGenerator());
    }

    default public String registerBean(Class<?> clazz, ConfigurableApplicationContext appContext, Class<?>... autoInjectVals) {
        return registerBean(clazz, appContext, new AnnotationBeanNameGenerator(), autoInjectVals);
    }

    default public String registerBean(Class<?> clazz, ConfigurableApplicationContext appContext, BeanNameGenerator generator) {
        return registerBean(clazz, appContext, new AnnotationBeanNameGenerator(), new Class<?>[]{});
    }

    default public String registerBean(Class<?> clazz, ConfigurableApplicationContext appContext, BeanNameGenerator generator,
                                       Class<?>... autoInjectVals) {
        if (appContext instanceof BeanDefinitionRegistry) {
            try {
                BeanDefinitionRegistry registry = (BeanDefinitionRegistry) appContext;
                SimpleMetadataReaderFactory factory = new SimpleMetadataReaderFactory(clazz.getClassLoader());
                MetadataReader metadtaReader = factory.getMetadataReader(clazz.getName());
                ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadtaReader);
                String beanName = generator.generateBeanName(sbd, registry);
                if (autoInjectVals != null) {
                    for (Class<?> injectClazz : autoInjectVals) {
                        String attrName = null;
                        for (Field field : clazz.getDeclaredFields()) {
                            if (field.getType().equals(injectClazz)) {
                                attrName = field.getName();
                                break;
                            }
                        }
                        if (attrName == null) {
                            throw new InitializeException(injectClazz + " no matching attributes were found");
                        }
                        ClassBeanReference clazzBeanDefinition = new ClassBeanReference(appContext.getBeanFactory(), injectClazz);
                        sbd.getPropertyValues().addPropertyValue(attrName, clazzBeanDefinition);
                    }
                }
                registry.registerBeanDefinition(beanName, sbd);
                return beanName;
            } catch (IOException e) {
                throw new InitializeException("", e);
            }
        } else {
            throw new IllegalArgumentException(appContext + " not instanceof BeanDefinitionRegistry");
        }
    }

    /**
     * 将某个包下的索引class都注入到bean 容器中
     *
     * @param packageName
     * @param appContext
     */
    default public void registerBean(String packageName, ConfigurableApplicationContext appContext) throws InitializeException {
        String basePackage = ClassUtils.convertClassNameToResourcePath(appContext.getEnvironment().resolveRequiredPlaceholders(packageName));
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + basePackage + "/*.class";
        try {
            Resource[] resources = appContext.getResources(packageSearchPath);
            MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> beanClass = Class.forName(className);
                registerBean(beanClass, appContext);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new InitializeException("register package bean fail", e);
        }
    }

    default public void registerBean(Package scanPackage, ConfigurableApplicationContext appContext) throws InitializeException {
        registerBean(scanPackage.getName(), appContext);
    }

    /**
     * 将包含目标注解的类注入到bean容器里，扫描的包是根据bee-core的scanPackage配置进行注入的
     *
     * @param annotationType
     * @param appContext
     */
    default public void registerBeanByAnnotationType(Class<? extends Annotation> annotationType, ConfigurableApplicationContext appContext) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) appContext;
        ResourceLoader resourceLoader = (ResourceLoader) appContext;
        ClassPathBeanDefinitionScanner interceptsScanner = new ClassPathBeanDefinitionScanner(registry);
        interceptsScanner.setResourceLoader(resourceLoader);
        interceptsScanner.addIncludeFilter(new AnnotationTypeFilter(annotationType));
        interceptsScanner.scan(getScanPackages(appContext));
    }

    /**
     * 将包含类注入到bean容器里，扫描的包是根据bee-core的scanPackage配置进行注入的
     *
     * @param includeFilter
     * @param appContext
     */
    default public void registerBean(TypeFilter includeFilter, ConfigurableApplicationContext appContext) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) appContext;
        ResourceLoader resourceLoader = (ResourceLoader) appContext;
        ClassPathBeanDefinitionScanner interceptsScanner = new ClassPathBeanDefinitionScanner(registry);
        interceptsScanner.setResourceLoader(resourceLoader);
        interceptsScanner.addIncludeFilter(includeFilter);
        interceptsScanner.scan(getScanPackages(appContext));
    }

    default public String[] getScanPackages(ConfigurableApplicationContext applicationContext) {
        String[] scanPackages;
        String scanPackage = System.getProperty("bee.config.scanPackage");
        List<String> scanpackageList = new ArrayList<String>();
        List<String> configSplitList = new ArrayList<String>();
        if (!StringUtil.isBlank(scanPackage)) {
            for (String pack : scanPackage.split(",")) {
                configSplitList.add(pack);
            }
        }
        String springContextScanPackage = applicationContext.getEnvironment().getProperty("bee.config.scanPackage");
        if (!StringUtil.isBlank(springContextScanPackage)) {
            for (String pack : springContextScanPackage.split(",")) {
                configSplitList.add(pack);
            }
        }
        for (String pack : configSplitList) {
            if (!scanpackageList.contains(pack)) {
                boolean ignore = false;
                for (String existPack : scanpackageList) {
                    if (pack.startsWith(existPack)) {
                        ignore = true;
                    } else if (existPack.startsWith(pack)) {
                        scanpackageList.remove(existPack);
                        break;
                    }
                }
                if (!ignore) {
                    scanpackageList.add(pack);
                }
            }
        }
        scanPackages = new String[scanpackageList.size()];
        scanpackageList.toArray(scanPackages);
        return scanPackages;
    }

    /**
     * 发布rpc服务，在发布rpc服务前，请确保该接口的实现已经注册过registerBean
     *
     * @param clazz      该class必须为接口
     * @param appContext
     * @return
     */
    default String exportRpc(Class<?> clazz, ConfigurableApplicationContext appContext) {
        return exportRpc(clazz, null, appContext);
    }

    /**
     * 发布rpc服务，在发布rpc服务前，请确保该接口的实现已经注册过registerBean
     *
     * @param clazz      该class必须为接口
     * @param rpcConfig  服务发布配置,kv格式 : k1=v1;k2=v2
     * @param appContext
     * @return
     */
    default String exportRpc(Class<?> clazz, String rpcConfig, ConfigurableApplicationContext appContext) {
        if (appContext instanceof BeanDefinitionRegistry) {
            try {
                BeanDefinitionRegistry registry = (BeanDefinitionRegistry) appContext;
                SimpleMetadataReaderFactory factory = new SimpleMetadataReaderFactory(clazz.getClassLoader());
                MetadataReader metadtaReader = factory.getMetadataReader(DServiceBean.class.getName());
                ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadtaReader);
                sbd.getPropertyValues().add("interfaceClass", clazz);
                String[] names = appContext.getBeanFactory().getBeanNamesForType(clazz);
                if (names == null || names.length == 0) {
                    throw new InitializeException(clazz + " implementing class not found");
                }
                if (names.length > 1) {
                    TComLogs.warn("find multiple implementation classes and choose the first {}", names[0]);
                }
                sbd.getPropertyValues().add("ref", new RuntimeBeanReference(names[0]));
                if (!StringUtil.isBlank(rpcConfig)) {
                    sbd.getPropertyValues().add("rpcConfig", rpcConfig);
                }
                String beanName = new DefaultBeanNameGenerator().generateBeanName(sbd, registry);
                registry.registerBeanDefinition(beanName, sbd);
                return beanName;
            } catch (IOException e) {
                throw new InitializeException("", e);
            }
        } else {
            throw new IllegalArgumentException(appContext + " not instanceof BeanDefinitionRegistry");
        }
    }

    /**
     * @param clazz
     * @param appContext
     * @return
     */
    default String referenceRpc(Class<?> clazz, ConfigurableApplicationContext appContext) {
        return referenceRpc(clazz, null, appContext);
    }

    /**
     * @param clazz
     * @param rpcConfig
     * @param appContext
     * @return
     */
    default String referenceRpc(Class<?> clazz, String rpcConfig, ConfigurableApplicationContext appContext) {
        if (appContext instanceof BeanDefinitionRegistry) {
            try {
                BeanDefinitionRegistry registry = (BeanDefinitionRegistry) appContext;
                SimpleMetadataReaderFactory factory = new SimpleMetadataReaderFactory(clazz.getClassLoader());
                MetadataReader metadtaReader = factory.getMetadataReader(DReferenceBean.class.getName());
                ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadtaReader);
                sbd.getPropertyValues().add("interfaceClass", clazz);
                if (!StringUtil.isBlank(rpcConfig)) {
                    sbd.getPropertyValues().add("rpcConfig", rpcConfig);
                }
                String beanName = new DefaultBeanNameGenerator().generateBeanName(sbd, registry);
                registry.registerBeanDefinition(beanName, sbd);
                return beanName;
            } catch (IOException e) {
                throw new InitializeException("", e);
            }
        } else {
            throw new IllegalArgumentException(appContext + " not instanceof BeanDefinitionRegistry");
        }
    }

    @SuppressWarnings("unchecked")
    default public <T> void findAnnotationOnClass(Class<?> clazz, Class<T> target, List<T> annotations) {
        if (clazz == null || clazz.equals(Object.class) || StringUtil.startsWith(clazz.getName(), "java")) {
            return;
        }
        for (Annotation annotation : clazz.getAnnotations()) {
            Class<?> annClass = annotation.annotationType();
            if (annClass.equals(target)) {
                annotations.add((T) annotation);
            }
            if (!annClass.equals(clazz)) {
                findAnnotationOnClass(annClass, target, annotations);
            }
        }
        findAnnotationOnClass(clazz.getSuperclass(), target, annotations);
        for (Class<?> intf : clazz.getInterfaces()) {
            findAnnotationOnClass(intf, target, annotations);
        }
    }

    default String getProperty(ConfigurableApplicationContext appContext, String key) {
        return appContext.getEnvironment().getProperty(key);
    }

    default String getProperty(ConfigurableApplicationContext appContext, String key, String def) {
        String val = getProperty(appContext, key);
        return val == null ? def : val;
    }

    default Boolean getBooleanProperty(ConfigurableApplicationContext appContext, String key) {
        return appContext.getEnvironment().getProperty(key, Boolean.class);
    }

    default Boolean getBooleanProperty(ConfigurableApplicationContext appContext, String key, Boolean def) {
        Boolean val = getBooleanProperty(appContext, key);
        return val == null ? def : val;
    }

    default Integer getIntProperty(ConfigurableApplicationContext appContext, String key) {
        return appContext.getEnvironment().getProperty(key, Integer.class);
    }

    default Integer getIntProperty(ConfigurableApplicationContext appContext, String key, Integer def) {
        Integer val = getIntProperty(appContext, key);
        return val == null ? def : val;
    }

    default Long getLongProperty(ConfigurableApplicationContext appContext, String key) {
        return appContext.getEnvironment().getProperty(key, Long.class);
    }

    default Long getLongProperty(ConfigurableApplicationContext appContext, String key, Long def) {
        Long val = getLongProperty(appContext, key);
        return val == null ? def : val;
    }

    default Float getFloatProperty(ConfigurableApplicationContext appContext, String key) {
        return appContext.getEnvironment().getProperty(key, Float.class);
    }

    default Float getFloatProperty(ConfigurableApplicationContext appContext, String key, Float def) {
        Float val = getFloatProperty(appContext, key);
        return val == null ? def : val;
    }
}
