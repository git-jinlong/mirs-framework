/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mirs.banxiaoxiao.framework.core.drpc.spring;

import static org.springframework.core.BridgeMethodResolver.findBridgedMethod;
import static org.springframework.core.BridgeMethodResolver.isVisibilityBridgeMethodPair;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.github.mirs.banxiaoxiao.framework.core.config.Constants;
import com.github.mirs.banxiaoxiao.framework.core.drpc.annotation.DS;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor} implementation that Consumer service {@link Reference} annotated fields
 *
 * @since 2.5.7
 */
public class DReferenceBeanFactory extends InstantiationAwareBeanPostProcessorAdapter implements MergedBeanDefinitionPostProcessor, PriorityOrdered,
        ApplicationContextAware, BeanClassLoaderAware, DisposableBean {

    /**
     * The bean name of {@link DReferenceBeanFactory}
     */
    public static final String BEAN_NAME = "dReferenceBeanFactory";

    private final Log logger = LogFactory.getLog(getClass());

    private ApplicationContext applicationContext;

    private ClassLoader classLoader;

    private final ConcurrentMap<String, ReferenceInjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<String, ReferenceInjectionMetadata>(
            256);

    private final ConcurrentMap<String, DReferenceBean<?>> referenceBeansCache = new ConcurrentHashMap<String, DReferenceBean<?>>();

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName)
            throws BeanCreationException {
        InjectionMetadata metadata = findReferenceMetadata(beanName, bean.getClass(), pvs);
        try {
            metadata.inject(bean, beanName, pvs);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of @Reference dependencies failed", ex);
        }
        return pvs;
    }

    /**
     * Finds {@link InjectionMetadata.InjectedElement} Metadata from annotated {@link Reference @Reference} fields
     *
     * @param beanClass
     *            The {@link Class} of Bean
     * @return non-null {@link List}
     */
    private List<ReferenceFieldElement> findFieldReferenceMetadata(final Class<?> beanClass) {
        final List<ReferenceFieldElement> elements = new LinkedList<ReferenceFieldElement>();
        ReflectionUtils.doWithFields(beanClass, new ReflectionUtils.FieldCallback() {

            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                DS reference = getAnnotation(field, DS.class);
                if (reference != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("@Reference annotation is not supported on static fields: " + field);
                        }
                        return;
                    }
                    elements.add(new ReferenceFieldElement(field, reference));
                }
            }
        });
        return elements;
    }

    /**
     * Finds {@link InjectionMetadata.InjectedElement} Metadata from annotated {@link Reference @Reference} methods
     *
     * @param beanClass
     *            The {@link Class} of Bean
     * @return non-null {@link List}
     */
    private List<ReferenceMethodElement> findMethodReferenceMetadata(final Class<?> beanClass) {
        final List<ReferenceMethodElement> elements = new LinkedList<ReferenceMethodElement>();
        ReflectionUtils.doWithMethods(beanClass, new ReflectionUtils.MethodCallback() {

            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                Method bridgedMethod = findBridgedMethod(method);
                if (!isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                    return;
                }
                DS reference = findAnnotation(bridgedMethod, DS.class);
                if (reference != null && method.equals(ClassUtils.getMostSpecificMethod(method, beanClass))) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("@Reference annotation is not supported on static methods: " + method);
                        }
                        return;
                    }
                    if (method.getParameterTypes().length == 0) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("@Reference  annotation should only be used on methods with parameters: " + method);
                        }
                    }
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, beanClass);
                    elements.add(new ReferenceMethodElement(method, pd, reference));
                }
            }
        });
        return elements;
    }

    /**
     * @param beanClass
     * @return
     */
    private ReferenceInjectionMetadata buildReferenceMetadata(final Class<?> beanClass) {
        Collection<ReferenceFieldElement> fieldElements = findFieldReferenceMetadata(beanClass);
        Collection<ReferenceMethodElement> methodElements = findMethodReferenceMetadata(beanClass);
        return new ReferenceInjectionMetadata(beanClass, fieldElements, methodElements);
    }

    private InjectionMetadata findReferenceMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
        // Fall back to class name as cache key, for backwards compatibility with custom callers.
        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
        // Quick check on the concurrent map first, with minimal locking.
        ReferenceInjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    try {
                        metadata = buildReferenceMetadata(clazz);
                        this.injectionMetadataCache.put(cacheKey, metadata);
                    } catch (NoClassDefFoundError err) {
                        throw new IllegalStateException("Failed to introspect bean class [" + clazz.getName()
                                + "] for reference metadata: could not find class that it depends on", err);
                    }
                }
            }
        }
        return metadata;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        if (beanType != null) {
            InjectionMetadata metadata = findReferenceMetadata(beanName, beanType, null);
            metadata.checkConfigMembers(beanDefinition);
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public void destroy() throws Exception {
        for (DReferenceBean<?> referenceBean : referenceBeansCache.values()) {
            if (logger.isInfoEnabled()) {
                logger.info(referenceBean + " was destroying!");
            }
            referenceBean.destroy();
        }
        injectionMetadataCache.clear();
        referenceBeansCache.clear();
        if (logger.isInfoEnabled()) {
            logger.info(getClass() + " was destroying!");
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Gets all beans of {@link ReferenceBean}
     *
     * @return non-null {@link Collection}
     * @since 2.5.9
     */
    public Collection<DReferenceBean<?>> getReferenceBeans() {
        return this.referenceBeansCache.values();
    }

    /**
     * {@link Reference} {@link InjectionMetadata} implementation
     *
     * @since 2.5.11
     */
    private static class ReferenceInjectionMetadata extends InjectionMetadata {

        private final Collection<ReferenceFieldElement> fieldElements;

        private final Collection<ReferenceMethodElement> methodElements;

        @SuppressWarnings("unchecked")
        public ReferenceInjectionMetadata(Class<?> targetClass, Collection<ReferenceFieldElement> fieldElements,
                                          Collection<ReferenceMethodElement> methodElements) {
            super(targetClass, combine(fieldElements, methodElements));
            this.fieldElements = fieldElements;
            this.methodElements = methodElements;
        }

        @SuppressWarnings("unchecked")
        private static <T> Collection<T> combine(Collection<? extends T>... elements) {
            List<T> allElements = new ArrayList<T>();
            for (Collection<? extends T> e : elements) {
                allElements.addAll(e);
            }
            return allElements;
        }

        public Collection<ReferenceFieldElement> getFieldElements() {
            return fieldElements;
        }

        public Collection<ReferenceMethodElement> getMethodElements() {
            return methodElements;
        }
    }

    /**
     * {@link Reference} {@link Method} {@link InjectionMetadata.InjectedElement}
     */
    private class ReferenceMethodElement extends InjectionMetadata.InjectedElement {

        private final Method method;

        private final DS reference;

        private volatile DReferenceBean<?> referenceBean;

        protected ReferenceMethodElement(Method method, PropertyDescriptor pd, DS reference) {
            super(method, pd);
            this.method = method;
            this.reference = reference;
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
            Class<?> referenceClass = pd.getPropertyType();
            referenceBean = buildReferenceBean(reference, referenceClass);
            ReflectionUtils.makeAccessible(method);
            method.invoke(bean, referenceBean.getObject());
        }
    }

    /**
     * {@link Reference} {@link Field} {@link InjectionMetadata.InjectedElement}
     */
    private class ReferenceFieldElement extends InjectionMetadata.InjectedElement {

        private final Field field;

        private final DS reference;

        private volatile DReferenceBean<?> referenceBean;

        protected ReferenceFieldElement(Field field, DS reference) {
            super(field, null);
            this.field = field;
            this.reference = reference;
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
            Class<?> referenceClass = field.getType();
            referenceBean = buildReferenceBean(reference, referenceClass);
            ReflectionUtils.makeAccessible(field);
            field.set(bean, referenceBean.getObject());
        }
    }

    private DReferenceBean<?> buildReferenceBean(DS reference, Class<?> referenceClass) throws Exception {
        try{
            // ?????????????????????????????????jvm?????????
            String jvmInstanceFrist = applicationContext.getEnvironment().getProperty(Constants.DRPC_JVMINSTANCEFRIST, "true");
            if(StringUtil.equals(jvmInstanceFrist, "true")) {
                Object jvmObject = applicationContext.getBean(referenceClass);
                if(jvmObject != null) {
                    return new JvmDReferenceBean(jvmObject);
                }
            }
        } catch(NoSuchBeanDefinitionException e) {
            // do nothing
        }
        String referenceBeanCacheKey = generateReferenceBeanCacheKey(reference, referenceClass);
        DReferenceBean<?> referenceBean = referenceBeansCache.get(referenceBeanCacheKey);
        if (referenceBean == null) {
            referenceBean = new DReferenceBean<Object>(reference);
            referenceBean.setInterface(resolveInterfaceName(reference, referenceClass));
            referenceBean.setApplicationContext(applicationContext);
            referenceBean.afterPropertiesSet();
            referenceBeansCache.putIfAbsent(referenceBeanCacheKey, referenceBean);
        }
        return referenceBean;
    }

    /**
     * Generate a cache key of {@link ReferenceBean}
     *
     * @param reference
     *            {@link Reference}
     * @param beanClass
     *            {@link Class}
     * @return
     */
    private String generateReferenceBeanCacheKey(DS reference, Class<?> beanClass) {
        String interfaceName = resolveInterfaceName(reference, beanClass);
        String key = "/" + interfaceName + "/" + reference.group();
        Environment environment = applicationContext.getEnvironment();
        key = environment.resolvePlaceholders(key);
        return key;
    }

    private static String resolveInterfaceName(DS reference, Class<?> beanClass) throws IllegalStateException {
        String interfaceName = null;
        if (!void.class.equals(reference.interfaceClass())) {
            interfaceName = reference.interfaceClass().getName();
        } else if (beanClass.isInterface()) {
            interfaceName = beanClass.getName();
        } else {
            Class<?>[] allInterfaces = beanClass.getInterfaces();
            if (allInterfaces.length > 0) {
                interfaceName = allInterfaces[0].getName();
            }
        }
        if (interfaceName == null) {
            throw new IllegalStateException("The @Reference undefined interfaceClass or interfaceName, and the property type " + beanClass.getName()
                    + " is not a interface.");
        }
        return interfaceName;
    }

    /**
     * Get {@link ReferenceBean} {@link Map} in injected field.
     *
     * @return non-null {@link Map}
     * @since 2.5.11
     */
    public Map<InjectionMetadata.InjectedElement, DReferenceBean<?>> getInjectedFieldReferenceBeanMap() {
        Map<InjectionMetadata.InjectedElement, DReferenceBean<?>> injectedElementReferenceBeanMap = new LinkedHashMap<InjectionMetadata.InjectedElement, DReferenceBean<?>>();
        for (ReferenceInjectionMetadata metadata : injectionMetadataCache.values()) {
            Collection<ReferenceFieldElement> fieldElements = metadata.getFieldElements();
            for (ReferenceFieldElement fieldElement : fieldElements) {
                injectedElementReferenceBeanMap.put(fieldElement, fieldElement.referenceBean);
            }
        }
        return injectedElementReferenceBeanMap;
    }

    /**
     * Get {@link ReferenceBean} {@link Map} in injected method.
     *
     * @return non-null {@link Map}
     * @since 2.5.11
     */
    public Map<InjectionMetadata.InjectedElement, DReferenceBean<?>> getInjectedMethodReferenceBeanMap() {
        Map<InjectionMetadata.InjectedElement, DReferenceBean<?>> injectedElementReferenceBeanMap = new LinkedHashMap<InjectionMetadata.InjectedElement, DReferenceBean<?>>();
        for (ReferenceInjectionMetadata metadata : injectionMetadataCache.values()) {
            Collection<ReferenceMethodElement> methodElements = metadata.getMethodElements();
            for (ReferenceMethodElement methodElement : methodElements) {
                injectedElementReferenceBeanMap.put(methodElement, methodElement.referenceBean);
            }
        }
        return injectedElementReferenceBeanMap;
    }

}
