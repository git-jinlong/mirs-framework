package com.github.mirs.banxiaoxiao.framework.core.log;

import org.slf4j.Logger;

/**
 * 为了规范日志输出，让日志打印更直观，更聚焦
 * 
 * @author zcy 2018年10月18日
 */
public interface FlowLogger {

    Logger defLogger();

    BizLogger bizLogger();

    ComLogger comLogger();

    TComLogger tcomLogger();

    TBizLogger tbizLogger();
    
    TBizLogger2 tbizLogger2();
}
