package com.github.mirs.banxiaoxiao.framework.core.log;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.github.mirs.banxiaoxiao.framework.core.log.flow.DefaultFlowLogger;

public class FlowLoggers {

    private static Map<String, FlowLogger> loggerCache = new HashMap<String, FlowLogger>();

    private static FlowLogger root = new DefaultFlowLogger(LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME));

    public static FlowLogger getLogger(Class<?> clazz) {
        return new DefaultFlowLogger(LoggerFactory.getLogger(clazz));
    }

    public static FlowLogger getLogger(String className) {
        FlowLogger childLogger = loggerCache.get(className);
        if (childLogger != null) {
            return childLogger;
        }
        synchronized (root) {
            FlowLogger logger = loggerCache.get(className);
            if (logger == null) {
                logger = new DefaultFlowLogger(LoggerFactory.getLogger(className));
                loggerCache.put(className, logger);
            }
            return logger;
        }
    }
}
