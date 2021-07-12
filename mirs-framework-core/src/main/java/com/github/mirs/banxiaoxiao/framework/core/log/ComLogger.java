package com.github.mirs.banxiaoxiao.framework.core.log;


public interface ComLogger {

    void debug(String formatMsg, Object... propertys);
    
    boolean isDebug();
    
    boolean isInfo();

    void info(String formatMsg, Object... propertys);

    void warn(String formatMsg, Object... propertys);

    void warn(String formatMsg, Throwable e, Object... propertys);

    void error(String formatMsg, Throwable e, Object... propertys);

    void error(String formatMsg, Object... propertys);
}
