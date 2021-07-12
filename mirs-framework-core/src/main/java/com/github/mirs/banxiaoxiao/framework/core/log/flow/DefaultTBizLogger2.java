package com.github.mirs.banxiaoxiao.framework.core.log.flow;

import org.slf4j.Logger;

import com.github.mirs.banxiaoxiao.framework.core.log.TBizLogger2;
import com.github.mirs.banxiaoxiao.framework.core.log.TimeWatch;

public class DefaultTBizLogger2 implements TBizLogger2 {

    private Logger logger;

    public DefaultTBizLogger2(Logger logger) {
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
            logger.debug(format(bizType, newArg(timeWatch, propertys)));
        }
    }

    @Override
    public void info(TimeWatch timeWatch, String bizType, Object... propertys) {
        if (logger.isInfoEnabled()) {
            logger.info(format(bizType, newArg2(timeWatch, propertys)));
        }
    }

    private String format(String bizType, Object... propertys) {
        return BizLoggerFormat.format2(bizType, propertys);
    }

    private Object[] newArg(TimeWatch timeWatch, Object... propertys) {
        Object[] obj;
        int length = propertys.length;
        if (propertys != null && length > 0) {
            obj = new Object[length + 2];
            System.arraycopy(propertys, 0, obj, 1, length);
            obj[0] = "timecost " + timeWatch.getCost();
            obj[propertys.length + 1] = timeWatch.outputTimeList();
        } else {
            obj = new Object[] { "timecost " + timeWatch.getCost(), timeWatch.outputTimeList() };
        }
        return obj;
    }

    private Object[] newArg2(TimeWatch timeWatch, Object... propertys) {
        Object[] obj;
        int length = propertys.length;
        if (propertys != null && length > 0) {
            obj = new Object[length + 1];
            System.arraycopy(propertys, 0, obj, 1, length);
            obj[0] = "timecost " + timeWatch.getCost();
        } else {
            obj = new Object[] { "timecost " + timeWatch.getCost() };
        }
        return obj;
    }
}
