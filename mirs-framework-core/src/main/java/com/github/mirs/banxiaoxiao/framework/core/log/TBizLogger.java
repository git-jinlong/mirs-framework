package com.github.mirs.banxiaoxiao.framework.core.log;

/**
 * log format : [className] bizType|timecost |propertys[0]=propertys[1]|[propertys2]=propertys[3]....
 * @author zcy 2018年10月19日
 */
public interface TBizLogger extends BizLogger {

    void debug(TimeWatch timeWatch, String bizType, Object... propertys);

    void info(TimeWatch timeWatch, String bizType, Object... propertys);
}
