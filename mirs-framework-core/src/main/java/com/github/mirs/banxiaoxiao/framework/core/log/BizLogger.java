package com.github.mirs.banxiaoxiao.framework.core.log;


public interface BizLogger {

    void debug(String bizType, Object... propertys);

    void info(String bizType, Object... propertys);

    void warn(String bizType, Object... propertys);

    void warn(String bizType, Throwable e, Object... propertys);

    void error(String bizType, Throwable e, Object... propertys);

    void error(String bizType, Object... propertys);
}
