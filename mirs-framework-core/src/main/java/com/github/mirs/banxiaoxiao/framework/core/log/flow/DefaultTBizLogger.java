package com.github.mirs.banxiaoxiao.framework.core.log.flow;

import org.slf4j.Logger;

import com.github.mirs.banxiaoxiao.framework.core.log.TBizLogger;
import com.github.mirs.banxiaoxiao.framework.core.log.TimeWatch;

public class DefaultTBizLogger implements TBizLogger {

    private Logger logger;

    public DefaultTBizLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void debug(String bizType, Object... propertys) {
        if (logger.isDebugEnabled()) {
            logger.debug(format(bizType, propertys));
        }
    }

    @Override
    public void info(String bizType, Object... propertys) {
        if (logger.isDebugEnabled()) {
            logger.debug(format(bizType, propertys));
        }
    }

    @Override
    public void warn(String bizType, Object... propertys) {
        if (logger.isWarnEnabled()) {
            logger.warn(format(bizType, propertys));
        }
    }

    @Override
    public void warn(String bizType, Throwable e, Object... propertys) {
        if (logger.isWarnEnabled()) {
            logger.warn(format(bizType, propertys), e);
        }
    }

    @Override
    public void error(String bizType, Throwable e, Object... propertys) {
        if (logger.isErrorEnabled()) {
            logger.error(format(bizType, propertys), e);
        }
    }

    @Override
    public void error(String bizType, Object... propertys) {
        if (logger.isErrorEnabled()) {
            logger.error(format(bizType, propertys));
        }
    }

    @Override
    public void debug(TimeWatch timeWatch, String bizType, Object... propertys) {
        if (logger.isDebugEnabled()) {
            logger.debug(format(bizType, propertys) + "|" + timeWatch.outputTimeList());
        }
    }

    @Override
    public void info(TimeWatch timeWatch, String bizType, Object... propertys) {
        if (logger.isInfoEnabled()) {
            logger.info(format(bizType, propertys) + "|" + timeWatch.outputTimeList());
        }
    }

    private String format(String bizType, Object... propertys) {
        return BizLoggerFormat.format(bizType, propertys);
    }
}
