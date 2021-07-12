package com.github.mirs.banxiaoxiao.framework.core.log.flow;

import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogger;
import com.github.mirs.banxiaoxiao.framework.core.log.TimeWatch;

public class DefaultTComLogger implements TComLogger {

    private Logger logger;

    public DefaultTComLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean isDebug() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfo() {
        return logger.isInfoEnabled();
    }
    
    @Override
    public void debug(String formatMsg, Object... propertys) {
        if (logger.isDebugEnabled()) {
            logger.debug(format(formatMsg, propertys));
        }
    }

    @Override
    public void info(String formatMsg, Object... propertys) {
        if (logger.isInfoEnabled()) {
            logger.info(format(formatMsg, propertys));
        }
    }

    @Override
    public void warn(String formatMsg, Object... propertys) {
        if (logger.isWarnEnabled()) {
            logger.warn(format(formatMsg, propertys));
        }
    }

    @Override
    public void warn(String formatMsg, Throwable e, Object... propertys) {
        if (logger.isWarnEnabled()) {
            logger.warn(format(formatMsg, propertys), e);
        }
    }

    @Override
    public void error(String formatMsg, Throwable e, Object... propertys) {
        if (logger.isErrorEnabled()) {
            logger.error(format(formatMsg, propertys), e);
        }
    }

    @Override
    public void error(String formatMsg, Object... propertys) {
        if (logger.isErrorEnabled()) {
            logger.error(format(formatMsg, propertys));
        }
    }

    @Override
    public void debug(TimeWatch timeWatch, String formatMsg, Object... propertys) {
        if (logger.isDebugEnabled()) {
            logger.debug(format(formatMsg, propertys) + " " + timeWatch.outputTimeList());
        }
    }

    @Override
    public void info(TimeWatch timeWatch, String formatMsg, Object... propertys) {
        if (logger.isInfoEnabled()) {
            logger.info(format(formatMsg, propertys) + " " + timeWatch.outputTimeList());
        }
    }

    private String format(String formatMsg, Object... propertys) {
        return MessageFormatter.arrayFormat(formatMsg, propertys).getMessage();
    }

}
