/**
 *
 */
package com.github.mirs.banxiaoxiao.framework.core.config;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.boot.Application;

import java.io.*;
import java.util.Properties;

/**
 * @author erxiao 2017年1月18日
 */
public class BeeClientLocalProperties extends Properties {

    /**
     *
     */
    private static final long serialVersionUID = -827905326646091100L;

    public static final String DEFAULT_BEECONFIG_NAME = "bee";

    private String scanPackage = "com.arcvideo";

    private String basePackage = "com.arcvideo";

    private String appName = "application";

    private String hostIndex = null;

    private String evn = "default";

    private String configServerUrl = "beec.inner.arcvideo.com:2181";

    private String configServerUsername = "user";

    private String configServerPassword;

    private Class<?> applicationClasss;

    public void load() {
        loadDefaultFromClass();
        loadDefaultFromBeeProperties();
        loadDefaultFromAppProperties();
        loadDefaultFromSystem();
    }

    public void loadFromXML(InputStream inputStream) {
        loadDefaultFromClass();
        try {
            super.loadFromXML(inputStream);
        } catch (IOException e) {
        }
    }

    public void load(Reader reader) {
        loadDefaultFromClass();
        try {
            super.load(reader);
        } catch (IOException e) {
        }
    }

    public void load(InputStream inputStream) {
        try {
            super.load(inputStream);
        } catch (IOException e) {
            // logger.warn("load property fail.");
        }
        resetDefault();
    }

    private void resetDefault() {
        String appNamePro = getProperty(Constants.CONFIG_APPNAME_KEY);
        String evnPro = getProperty(Constants.CONFIG_EVN_KEY);
        String configServerUrlPro = getProperty(Constants.CONFIG_SERVER_URL_KEY);
        String configServerUsernamePro = getProperty(Constants.CONFIG_SERVER_USERNAME_KEY);
        String configServerPasswordPro = getProperty(Constants.CONFIG_SERVER_PASSWORD_KEY);
        String scanPackageNamePro = getProperty(Constants.CONFIG_SCANPACKAGE_KEY);
        String hostIndex = getProperty(Constants.CONFIG_HOSTINDEX_KEY);
        if (!StringUtil.isBlank(scanPackageNamePro)) {
            this.scanPackage = scanPackageNamePro;
        }
        if (StringUtil.isBlank(this.appName)) {
            this.appName = appNamePro;
        }
        if (!StringUtil.isBlank(evnPro)) {
            this.evn = evnPro;
        }
        if (!StringUtil.isBlank(configServerUrlPro)) {
            this.configServerUrl = configServerUrlPro;
        }
        if (!StringUtil.isBlank(configServerUsernamePro)) {
            this.configServerUsername = configServerUsernamePro;
        }
        if (!StringUtil.isBlank(configServerPasswordPro)) {
            this.configServerPassword = configServerPasswordPro;
        }
        if (!StringUtil.isBlank(hostIndex)) {
            this.hostIndex = hostIndex;
        }
        if (StringUtil.isBlank(this.appName)) {
            throw new IllegalArgumentException("the application not found.");
        }
        if (StringUtil.isBlank(this.configServerUrl)) {
            throw new IllegalArgumentException("the config server not found.");
        }
        setProperty(Constants.CONFIG_APPNAME_KEY, this.appName);
    }

    protected void loadDefaultFromBeeProperties() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("bee.properties");
        if (inputStream != null) {
            load(inputStream);
        }
    }

    protected void loadDefaultFromAppProperties() {
        if (!StringUtil.isBlank(this.appName)) {
            InputStream appSelfProperties = this.getClass().getClassLoader().getResourceAsStream(this.appName + ".properties");
            if (appSelfProperties != null) {
                load(appSelfProperties);
            }
        }
        // 先找找默认的 configs/appname.properties
        InputStream coustemProperties = null;
        String coustemFile = "configs/" + this.appName + ".properties";
        try {
            coustemProperties = new FileInputStream(coustemFile);
        } catch (FileNotFoundException e) {
        }
        if (coustemProperties == null) {
            coustemFile = System.getProperty("bee.config.location");
            if (StringUtil.isBlank(coustemFile)) {
                coustemFile = System.getProperty("spring.config.location");
            }
            if (!StringUtil.isBlank(coustemFile)) {
                coustemProperties = this.getClass().getClassLoader().getResourceAsStream(coustemFile);
                if (coustemProperties == null) {
                    try {
                        coustemProperties = new FileInputStream(coustemFile);
                    } catch (FileNotFoundException e) {
                    }
                }
            }
        }
        if (coustemProperties != null) {
            load(coustemProperties);
        }
    }

    protected void loadDefaultFromSystem() {
        Properties p = System.getProperties();
        this.putAll(p);
        this.putAll(System.getenv());
    }

    @SuppressWarnings({"restriction", "deprecation"})
    protected void loadDefaultFromClass() {
        Class<?> parentClass = null;
        Application startClass = null;
        int i = 1;
        while ((parentClass = sun.reflect.Reflection.getCallerClass(i)) != null) {
            if (parentClass != null) {
                startClass = parentClass.getAnnotation(Application.class);
                if (startClass != null) {
                    break;
                }
            }
            i++;
        }
        if (startClass != null) {
            this.applicationClasss = parentClass;
            this.appName = startClass.value();
            // 设置默认的basePackage
            this.basePackage = parentClass.getPackage().getName();
            if (startClass.scanPackage() != null && startClass.scanPackage().length > 0) {
                this.scanPackage = StringUtil.join(startClass.scanPackage(), ",");
            }
        }
    }

    public String getAppName() {
        return appName;
    }

    public String getEvn() {
        return evn;
    }

    public void setEvn(String evn) {
        this.evn = evn;
    }

    public String getConfigServerUrl() {
        return configServerUrl;
    }

    public String getConfigServerUsername() {
        return configServerUsername;
    }

    public String getConfigServerPassword() {
        return configServerPassword;
    }

    public String getScanPackage() {
        return scanPackage;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public String getHostIndex() {
        return hostIndex;
    }

    public Class<?> getApplicationClasss() {
        return applicationClasss;
    }
}
