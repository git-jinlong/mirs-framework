/**
 *
 */
package com.github.mirs.banxiaoxiao.framework.core.boot;

import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientLocalProperties;
import com.github.mirs.banxiaoxiao.framework.core.config.Constants;
import com.github.mirs.banxiaoxiao.framework.common.util.IOUtils;
import com.github.mirs.banxiaoxiao.framework.core.encryt.CipherUtil;
import com.github.mirs.banxiaoxiao.framework.core.encryt.DecryptClassLoader;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bc
 *
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan({"com.github.mirs", "${bee.config.scanPackage}"})
public class MirsStarter {

    public static void run(final Object resrouce, String args[]) {
        setDefaultProperty();
        runSpring(resrouce, args);
    }

    private static void runSpring(final Object resource, String args[]) {
        registerEncrypt();
        SpringApplicationBuilder builder = new SpringApplicationBuilder((Class<?>) resource);
        SpringApplication app = builder.build();
        final ConfigurableApplicationContext appContext = app.run(args);
        Runtime.getRuntime().addShutdownHook(new Thread() {

            public void run() {
                synchronized (resource) {
                    SpringApplication.exit(appContext);
                    resource.notify();
                }
            }
        });
        synchronized (resource) {
            while (true) {
                try {
                    resource.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private static void registerEncrypt() {
        if (CipherUtil.isEnable()) {
            String classReaderName = "org.springframework.asm.ClassReader";
            DecryptClassLoader classLoader = new DecryptClassLoader(Thread.currentThread().getContextClassLoader());
            try {
                ClassPathResource classRes = new ClassPathResource("ClassReader");
                byte[] clazzBytes = IOUtils.readAllBytes(classRes.getInputStream());
                byte[] decryptBytes = CipherUtil.decryptcr(clazzBytes);
                classLoader.define(classReaderName, decryptBytes);
            } catch (Exception e) {
                TComLogs.error("", e);
                throw new InitializeException("", e);
            }
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }

    private static void setDefaultProperty() {
        System.setProperty("spring.cloud.config.enabled", "false");
        BeeClientLocalProperties localProperties = BeeClientConfiguration.getLocalProperies();
        Map<String, String> newMap = new HashMap<String, String>();
        for (Object key : localProperties.keySet()) {
            String propertyKey = (String) key;
            if (propertyKey.startsWith(Constants.LOG_PREFIX)) {
                System.setProperty(propertyKey, (String) localProperties.getProperty(propertyKey));
            }
            if (propertyKey.startsWith("logging.level")) {
                System.setProperty(propertyKey, (String) localProperties.getProperty(propertyKey));
            }
            if (propertyKey.startsWith(Constants.LOG_LEVEL + ".")) {
                String loggingKey = propertyKey.replaceAll(Constants.LOG_LEVEL_REGX, "logging.level");
                newMap.put(loggingKey, localProperties.getProperty(propertyKey));
                System.setProperty(loggingKey, localProperties.getProperty(propertyKey));
            }

            //只对dubbo参数做一下特殊处理，为了解决dubbo连zk时设置的用户名密码参数无法提前注入到spring context中的问题
            if (propertyKey.startsWith("dubbo.")) {
                System.setProperty(propertyKey, localProperties.getProperty(propertyKey));
            }
        }
        localProperties.putAll(newMap);
        System.setProperty(Constants.CONFIG_APPNAME_KEY, BeeClientConfiguration.getLocalProperies().getAppName());
    }
}
