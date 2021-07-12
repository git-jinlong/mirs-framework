package com.github.mirs.banxiaoxiao.framework.core.log;

/**
 * 通用日志器
 */
public class TBizLogs {

    /**
     * 获取适配日志器，供内部调用
     * 
     * @return
     */
    @SuppressWarnings({ "restriction", "deprecation" })
    private static TBizLogger getLocationAwareLogger(final int depth) {
        String className = sun.reflect.Reflection.getCallerClass(depth).getName();
        return FlowLoggers.getLogger(className).tbizLogger();
    }

    /**
     * 静态的获取日志器
     * 
     * @return
     */
    private static TBizLogger getLogger() {
        return getLocationAwareLogger(4);
    }

    public static void debug(String bizType, Object... propertys) {
        getLogger().debug(bizType, propertys);
    }

    public static void info(String bizType, Object... propertys) {
        getLogger().info(bizType, propertys);
    }

    public static void warn(String bizType, Object... propertys) {
        getLogger().warn(bizType, propertys);
    }

    public static void warn(String bizType, Throwable e, Object... propertys) {
        getLogger().warn(bizType, e, propertys);
    }

    public static void error(String bizType, Throwable e, Object... propertys) {
        getLogger().error(bizType, e, propertys);
    }

    public static void error(String bizType, Object... propertys) {
        getLogger().error(bizType, propertys);
    }

    public static void debug(TimeWatch timeWatch, String bizType, Object... propertys) {
        getLogger().debug(timeWatch, bizType, propertys);
    }

    public static void info(TimeWatch timeWatch, String bizType, Object... propertys) {
        getLogger().info(timeWatch, bizType, propertys);
    }
}
