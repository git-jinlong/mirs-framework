package com.github.mirs.banxiaoxiao.framework.core.log.flow;

import org.slf4j.Logger;

import com.github.mirs.banxiaoxiao.framework.core.log.BizLogger;
import com.github.mirs.banxiaoxiao.framework.core.log.ComLogger;
import com.github.mirs.banxiaoxiao.framework.core.log.FlowLogger;
import com.github.mirs.banxiaoxiao.framework.core.log.TBizLogger;
import com.github.mirs.banxiaoxiao.framework.core.log.TBizLogger2;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogger;

public class DefaultFlowLogger implements FlowLogger {

    private Logger logger;

    private DefaultTBizLogger tbizLogger;
    
    private DefaultTBizLogger2 tbizLogger2;

    private DefaultTComLogger tcomLogger;

    public DefaultFlowLogger(Logger logger) {
        this.logger = logger;
        this.tbizLogger = new DefaultTBizLogger(logger);
        this.tcomLogger = new DefaultTComLogger(logger);
        this.tbizLogger2 = new DefaultTBizLogger2(logger);
    }

    @Override
    public Logger defLogger() {
        return logger;
    }

    @Override
    public BizLogger bizLogger() {
        return tbizLogger;
    }

    @Override
    public ComLogger comLogger() {
        return tcomLogger;
    }

    @Override
    public TComLogger tcomLogger() {
        return tcomLogger;
    }

    @Override
    public TBizLogger tbizLogger() {
        return tbizLogger;
    }

    @Override
    public TBizLogger2 tbizLogger2() {
        return tbizLogger2;
    }
}
