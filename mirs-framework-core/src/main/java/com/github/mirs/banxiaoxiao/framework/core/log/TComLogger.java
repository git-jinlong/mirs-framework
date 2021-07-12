package com.github.mirs.banxiaoxiao.framework.core.log;

public interface TComLogger extends ComLogger {

    void debug(TimeWatch timeWatch, String formatMsg, Object... propertys);

    void info(TimeWatch timeWatch, String formatMsg, Object... propertys);
}
