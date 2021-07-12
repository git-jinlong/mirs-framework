package com.github.mirs.banxiaoxiao.framework.common.util;

import org.slf4j.Logger;

/**
 * <pre>
 * 格式化输出业务日志内容工具，输出格式的规范为:  log_type|propertyKey=propertyValue|...
 * 之所以要按此格式化，是为了让开发人员查看日志时更聚焦在关键数据上
 * </pre>
 * 
 * @author zcy 2018年8月27日
 */
public class BizLoggerFormat {

    public static String SEPARATOR = "|";

    public static String SEPARATOR_PROPERTY = "=";

    /**
     * @param logType
     * @param strings
     *            数组必须是2的倍数，根据规范key和value必须成对出现
     * @return
     */
    public static String format(String logType, Object... propertys) {
        StringBuilder sb = new StringBuilder();
        sb.append(logType);
        if (propertys != null && propertys.length > 0) {
            sb.append(SEPARATOR);
            int length = propertys.length;
            for (int i = 1; i < length + 1; i++) {
                sb.append(propertys[i - 1]);
                if (i < length) {
                    if (i % 2 == 0) {
                        sb.append(SEPARATOR);
                    } else {
                        sb.append(SEPARATOR_PROPERTY);
                    }
                }
            }
        }
        return sb.toString();
    }

    public static void debug(Logger logger, String logType, Object... propertys) {
        if (logger.isDebugEnabled()) {
            logger.debug(format(logType, propertys));
        }
    }

    public static void info(Logger logger, String logType, Object... propertys) {
        if (logger.isInfoEnabled()) {
            logger.debug(format(logType, propertys));
        }
    }

    public static void warn(Logger logger, String logType, Object... propertys) {
        if (logger.isWarnEnabled()) {
            logger.warn(format(logType, propertys));
        }
    }

    public static void error(Logger logger, String logType, Throwable e, Object... propertys) {
        if (logger.isErrorEnabled()) {
            logger.error(format(logType, propertys), e);
        }
    }
}
