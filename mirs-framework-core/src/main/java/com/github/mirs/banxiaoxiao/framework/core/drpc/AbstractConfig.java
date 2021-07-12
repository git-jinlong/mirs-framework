package com.github.mirs.banxiaoxiao.framework.core.drpc;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.dubbo.common.utils.ConfigUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mirs.banxiaoxiao.framework.core.drpc.annotation.DS;

/**
 * <pre>
 * 基于dubbo rpc的动态group服务治理，在dubbo强大的rpc能力之上，提供group根据需要动态变化的能力
 * </pre>
 * 
 * @author zcy 2019年3月21日
 */
public class AbstractConfig implements Serializable {

    /** */
    private static final long serialVersionUID = 4553127439968542937L;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final String[] SUFFIXES = new String[] { "Config", "Bean" };

    private static final Map<String, String> legacyProperties = new HashMap<String, String>();
    static {
        legacyProperties.put("dubbo.protocol.name", "dubbo.service.protocol");
        legacyProperties.put("dubbo.protocol.host", "dubbo.service.server.host");
        legacyProperties.put("dubbo.protocol.port", "dubbo.service.server.port");
        legacyProperties.put("dubbo.protocol.threads", "dubbo.service.max.thread.pool.size");
        legacyProperties.put("dubbo.consumer.timeout", "dubbo.service.invoke.timeout");
        legacyProperties.put("dubbo.consumer.retries", "dubbo.service.max.retry.providers");
        legacyProperties.put("dubbo.consumer.check", "dubbo.service.allow.no.provider");
        legacyProperties.put("dubbo.service.url", "dubbo.service.address");
    }

    private String group;

    private Class<?> groupLoaderClass;

    private Map<String, String> rpcConfig;

    private DServiceGroupLoader groupLoader;

    public AbstractConfig() {
    }

    public AbstractConfig(String group, Class<?> groupLoader, String rpcKvConfig) {
        setRpcConfig(rpcKvConfig);
        this.group = group;
        this.groupLoaderClass = groupLoader;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Class<?> getGroupLoaderClass() {
        return groupLoaderClass;
    }

    public void setGroupLoaderClass(Class<?> groupLoaderClass) {
        this.groupLoaderClass = groupLoaderClass;
    }

    public Map<String, String> getRpcConfig() {
        return rpcConfig;
    }

    public void setRpcConfig(Map<String, String> rpcConfig) {
        this.rpcConfig = rpcConfig;
    }

    public String getRpcConfig(String key) {
        if (this.rpcConfig == null) {
            return null;
        } else {
            return this.rpcConfig.get(key);
        }
    }

    public void setRpcConfig(String configKv) {
        if (configKv != null && configKv.trim().length() > 0) {
            this.rpcConfig = new HashMap<String, String>();
            for (String kvStr : configKv.split(";")) {
                String[] kv = kvStr.split("=");
                if (kv.length == 2) {
                    this.rpcConfig.put(kv[0], kv[1]);
                }
            }
        }
    }

    protected String getGroupName(DS service) {
        DServiceGroupLoader loader = null;
        if (!void.class.equals(service.groupLoader())) {
            try {
                loader = (DServiceGroupLoader) service.groupLoader().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ConfigException("get group loader", e);
            }
        }
        if (loader == null) {
            loader = getGroupLoader();
        }
        if (loader != null) {
            return loader.getGroup();
        } else {
            if (this.group == null || this.group.trim().isEmpty()) {
                return service.group();
            } else {
                return this.group;
            }
        }
    }

    public void setGroupLoader(DServiceGroupLoader groupLoader) {
        this.groupLoader = groupLoader;
    }

    protected DServiceGroupLoader getGroupLoader() {
        if (this.groupLoader != null) {
            return this.groupLoader;
        }
        if (!void.class.equals(this.groupLoaderClass) && this.groupLoaderClass != null) {
            DServiceGroupLoader loader = null;
            try {
                loader = (DServiceGroupLoader) this.groupLoaderClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ConfigException("get group loader", e);
            }
            return loader;
        }
        return null;
    }

    protected String getGroupName() {
        DServiceGroupLoader loader = getGroupLoader();
        if (loader != null) {
            return loader.getGroup();
        } else {
            return group;
        }
    }

    protected void setDubboConfigValue(org.apache.dubbo.config.AbstractConfig config) {
        if (config == null) {
            return;
        }
        String prefix = "dubbo." + getTagName(config.getClass()) + ".";
        Method[] methods = config.getClass().getMethods();
        for (Method method : methods) {
            try {
                String name = method.getName();
                if (name.length() > 3 && name.startsWith("set") && Modifier.isPublic(method.getModifiers()) && method.getParameterTypes().length == 1
                        && isPrimitive(method.getParameterTypes()[0])) {
                    String property = StringUtils.camelToSplitName(name.substring(3, 4).toLowerCase() + name.substring(4), ".");
                    String value = null;
                    if (property.equalsIgnoreCase("group")) {
                        value = getGroupName();
                    }
                    if (value == null || value.length() == 0) {
                        value = getRpcConfig(property);
                    }
                    if (value == null || value.length() == 0) {
                        value = getRpcConfig(property);
                        if (config.getId() != null && config.getId().length() > 0) {
                            String pn = prefix + config.getId() + "." + property;
                            value = getRpcConfig(pn);
                        }
                    }
                    if (value == null || value.length() == 0) {
                        value = getRpcConfig(property);
                        String pn = prefix + property;
                        value = getRpcConfig(pn);
                    }
                    if (value == null || value.length() == 0) {
                        if (config.getId() != null && config.getId().length() > 0) {
                            String pn = prefix + config.getId() + "." + property;
                            value = System.getProperty(pn);
                        }
                    }
                    if (value == null || value.length() == 0) {
                        String pn = prefix + property;
                        value = System.getProperty(pn);
                    }
                    if (value == null || value.length() == 0) {
                        Method getter;
                        try {
                            getter = config.getClass().getMethod("get" + name.substring(3), new Class<?>[0]);
                        } catch (NoSuchMethodException e) {
                            try {
                                getter = config.getClass().getMethod("is" + name.substring(3), new Class<?>[0]);
                            } catch (NoSuchMethodException e2) {
                                getter = null;
                            }
                        }
                        if (getter != null) {
                            if (getter.invoke(config, new Object[0]) == null) {
                                if (config.getId() != null && config.getId().length() > 0) {
                                    value = ConfigUtils.getProperty(prefix + config.getId() + "." + property);
                                }
                                if (value == null || value.length() == 0) {
                                    value = ConfigUtils.getProperty(prefix + property);
                                }
                                if (value == null || value.length() == 0) {
                                    String legacyKey = legacyProperties.get(prefix + property);
                                    if (legacyKey != null && legacyKey.length() > 0) {
                                        value = convertLegacyValue(legacyKey, ConfigUtils.getProperty(legacyKey));
                                    }
                                }
                            }
                        }
                    }
                    if (value != null && value.length() > 0) {
                        method.invoke(config, new Object[] { convertPrimitive(method.getParameterTypes()[0], value) });
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static boolean isPrimitive(Class<?> type) {
        return type.isPrimitive() || type == String.class || type == Character.class || type == Boolean.class || type == Byte.class
                || type == Short.class || type == Integer.class || type == Long.class || type == Float.class || type == Double.class
                || type == Object.class;
    }

    public static String convertLegacyValue(String key, String value) {
        if (value != null && value.length() > 0) {
            if ("dubbo.service.max.retry.providers".equals(key)) {
                return String.valueOf(Integer.parseInt(value) - 1);
            } else if ("dubbo.service.allow.no.provider".equals(key)) {
                return String.valueOf(!Boolean.parseBoolean(value));
            }
        }
        return value;
    }

    public static Object convertPrimitive(Class<?> type, String value) {
        if (type == char.class || type == Character.class) {
            return value.length() > 0 ? value.charAt(0) : '\0';
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.valueOf(value);
        } else if (type == byte.class || type == Byte.class) {
            return Byte.valueOf(value);
        } else if (type == short.class || type == Short.class) {
            return Short.valueOf(value);
        } else if (type == int.class || type == Integer.class) {
            return Integer.valueOf(value);
        } else if (type == long.class || type == Long.class) {
            return Long.valueOf(value);
        } else if (type == float.class || type == Float.class) {
            return Float.valueOf(value);
        } else if (type == double.class || type == Double.class) {
            return Double.valueOf(value);
        }
        return value;
    }

    public static String getTagName(Class<?> cls) {
        String tag = cls.getSimpleName();
        for (String suffix : SUFFIXES) {
            if (tag.endsWith(suffix)) {
                tag = tag.substring(0, tag.length() - suffix.length());
                break;
            }
        }
        tag = tag.toLowerCase();
        return tag;
    }
}
