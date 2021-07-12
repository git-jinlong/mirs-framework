package com.github.mirs.banxiaoxiao.framework.core.boot;

import com.github.mirs.banxiaoxiao.framework.common.util.IOUtils;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.config.Constants;
import com.github.mirs.banxiaoxiao.framework.core.encryt.CipherUtil;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.core.script.Script;
import com.github.mirs.banxiaoxiao.framework.core.script.ScriptHelper;
import com.github.mirs.banxiaoxiao.framework.core.script.ScriptVariableProcessor;
import com.github.mirs.banxiaoxiao.framework.core.script.impl.ScriptFactory;
import com.github.mirs.banxiaoxiao.framework.core.spring.SpringContextHolder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author zcy 2018年9月18日
 */
public class BootstrapInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    @Override
    public void initialize(ConfigurableApplicationContext appContext) {
        TComLogs.info("enable xxx={}", CipherUtil.isEnable());
        Class<?> applicationClass = BeeClientConfiguration.getLocalProperies().getApplicationClasss();
        Properties systemDef = new Properties();
        String appName = BeeClientConfiguration.getLocalProperies().getAppName();
        String basePackage = BeeClientConfiguration.getLocalProperies().getBasePackage();
        String scanPackage = BeeClientConfiguration.getLocalProperies().getScanPackage();
        if (scanPackage == null || scanPackage.trim().startsWith("${")) {
            scanPackage = basePackage;
        }
        systemDef.putAll(BeeClientConfiguration.getLocalProperies());
        systemDef.put(Constants.CONFIG_APPNAME_KEY, appName);
        systemDef.put(Constants.CONFIG_BASEPACKAGE_KEY, basePackage);
        systemDef.put(Constants.CONFIG_SCANPACKAGE_KEY, scanPackage);
        PropertiesPropertySource ps = new PropertiesPropertySource("INITIALIZER", systemDef);
        appContext.getEnvironment().getPropertySources().addFirst(ps);
        if (appContext.getParent() != null) {
            return;
        }
        SpringContextHolder.set(appContext);
        // 初始化脚本执行器
        initScriptProcessor(appContext);
        // 获取所有的组件开启注解
        Map<EnableInitializer, Annotation> initializerAnnotations = new HashMap<EnableInitializer, Annotation>();
        getEnableInitializer(initializerAnnotations);
        getEnableInitializer(null, applicationClass, initializerAnnotations);
        Map<ApplicationInitializer, EnableInitializer> initializerMap = new HashMap<ApplicationInitializer, EnableInitializer>();
        Map<ApplicationInitializer, Annotation> enableAnnotationMap = new HashMap<ApplicationInitializer, Annotation>();
        List<ApplicationInitializer> initializers = new ArrayList<>();
        List<Class<?>> clazzSet = new ArrayList<Class<?>>();
        for (Entry<EnableInitializer, Annotation> entry : initializerAnnotations.entrySet()) {
            EnableInitializer initializerAnnotation = entry.getKey();
            Annotation enableAnnotation = entry.getValue();
            for (Class<?> initializerClass : initializerAnnotation.value()) {
                if (!clazzSet.contains(initializerClass)) {
                    clazzSet.add(initializerClass);
                    try {
                        ApplicationInitializer initializer = (ApplicationInitializer) initializerClass.newInstance();
                        initializers.add(initializer);
                        TComLogs.info("finded [{}]", initializer);
                        initializerMap.put(initializer, initializerAnnotation);
                        enableAnnotationMap.put(initializer, enableAnnotation);
                        appContext.getBeanFactory().registerSingleton(genInitializerBeanName(initializer), initializer);
                    } catch (Exception e) {
                        throw new InitializeException("", e);
                    }
                }
            }
        }
        initializers.sort(new Comparator<ApplicationInitializer>() {

            @Override
            public int compare(ApplicationInitializer arg0, ApplicationInitializer arg1) {
                return arg0.order() - arg1.order();
            }
        });
        for (ApplicationInitializer initializer : initializers) {
            try {
                EnableInitializer initializerAnnotation = initializerMap.get(initializer);
                boolean conditionMatch = evalCondition(initializerAnnotation.condition());
                if (conditionMatch) {
                    if (initializer instanceof ModuleInitializer) {
                        Annotation enableAnnotation = enableAnnotationMap.get(initializer);
                        ModuleInitializer moduleInitializer = (ModuleInitializer) initializer;
                        if (enableAnnotation != null) {
                            moduleInitializer.init(enableAnnotation, appContext);
                        }
                        moduleInitializer.init(appContext);
                    } else {
                        initializer.init(appContext);
                    }
                    TComLogs.info("[{}] initialized", initializer.getClass().getName());
                } else {
                    TComLogs.info("[{}] not match condition {}", initializer.getClass().getName(), initializerAnnotation.condition());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initScriptProcessor(final ConfigurableApplicationContext applicationContext) {
        Script javaScript = ScriptFactory.getScript("JavaScript", new ScriptVariableProcessor() {

            @Override
            public Object process(String var) {
                String varVal = applicationContext.getEnvironment().getProperty(var);
                if (varVal == null || varVal.startsWith("$")) {
                    varVal = null;
                }
                if (varVal == null) {
                    varVal = System.getenv(var);
                }
                return varVal;
            }
        });
        applicationContext.getBeanFactory().registerSingleton("javaScript", javaScript);
        ScriptHelper.inject(javaScript);
    }

    private boolean evalCondition(String condition) {
        if (StringUtil.isBlank(condition)) {
            return true;
        }
        boolean match = true;
        try {
            Object obj = ScriptHelper.getJavaScript().eval(condition);
            if (obj == null) {
                match = false;
            } else {
                match = Boolean.parseBoolean(obj.toString());
            }
        } catch (Throwable e) {
            TComLogs.warn(e.getMessage());
            match = false;
        }
        return match;
    }

    private void getEnableInitializer(Annotation parent, Class<?> clazz, Map<EnableInitializer, Annotation> initializerAnnotations) {
        if (clazz == null || clazz.equals(Object.class) || StringUtil.startsWith(clazz.getName(), "java")) {
            return;
        }
        for (Annotation annotation : clazz.getAnnotations()) {
            Class<?> annClass = annotation.annotationType();
            if (annClass.equals(EnableInitializer.class)) {
                initializerAnnotations.put((EnableInitializer) annotation, parent);
            } else if (annClass.equals(EnableInitializers.class)) {
                EnableInitializers enableInitializers = (EnableInitializers) annotation;
                if (enableInitializers.value() != null) {
                    for (EnableInitializer enableInitializer : enableInitializers.value()) {
                        initializerAnnotations.put(enableInitializer, parent);
                    }
                }
            }
            if (!annClass.equals(clazz)) {
                getEnableInitializer(annotation, annClass, initializerAnnotations);
            }
        }
        getEnableInitializer(parent, clazz.getSuperclass(), initializerAnnotations);
        for (Class<?> intf : clazz.getInterfaces()) {
            getEnableInitializer(parent, intf, initializerAnnotations);
        }
    }

    /**
     * @param initializerAnnotations
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void getEnableInitializer(Map<EnableInitializer, Annotation> initializerAnnotations) {
        ResourcePatternResolver rr = new PathMatchingResourcePatternResolver();
        try {
            Resource[] rs = rr.getResources("classpath*:META-INF/com.github.mirs.banxiaoxiao.framework.core.boot.ApplicationInitializer");
            if (rs != null) {
                for (Resource r : rs) {
                    List<String> lines = IOUtils.readAllLines(new BufferedReader(new InputStreamReader(r.getInputStream())));
                    if (lines != null) {
                        for (String line : lines) {
                            String className = null;
                            String conditiont = null;
                            int emptyIndex = line.indexOf(" ");
                            if (emptyIndex < 0) {
                                className = line;
                            } else {
                                if (emptyIndex > 0) {
                                    className = line.substring(0, emptyIndex);
                                }
                                if (line.length() > emptyIndex) {
                                    conditiont = line.substring(emptyIndex).trim();
                                }
                            }
                            Class initializerClasst = null;
                            try {
                                initializerClasst = Class.forName(className);
                            } catch (ClassNotFoundException e) {
                                throw new InitializeException("META-INF/bee/com.github.mirs.banxiaoxiao.framework.core.boot.ApplicationInitializer " + line + " "
                                        + e.getMessage(), e);
                            }
                            final String condition = StringUtil.isBlank(conditiont) ? "true" : conditiont;
                            final Class<? extends ApplicationInitializer>[] initers = new Class[]{initializerClasst};
                            EnableInitializer initializer = new EnableInitializer() {

                                @Override
                                public Class<? extends Annotation> annotationType() {
                                    return EnableInitializer.class;
                                }

                                @Override
                                public Class<? extends ApplicationInitializer>[] value() {
                                    return initers;
                                }

                                @Override
                                public String condition() {
                                    return condition;
                                }
                            };
                            initializerAnnotations.put(initializer, null);
                        }
                    }
                }
            }
        } catch (IOException e) {
            TComLogs.warn("", e);
        }
    }

    private String genInitializerBeanName(ApplicationInitializer initializer) {
        return StringUtil.uncapitalize(initializer.getClass().getSimpleName());
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }
}
