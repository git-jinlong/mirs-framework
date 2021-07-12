package com.github.mirs.banxiaoxiao.framework.core.log.dlevel;

import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.dcc.conf.BaseDConfig;
import com.github.mirs.banxiaoxiao.framework.core.dcc.conf.DConfigListener;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

/**
 * @author zcy 2020年1月21日
 */
public class DynamicLevelListener extends BaseDConfig<String> implements DConfigListener<String> {

    public DynamicLevelListener() {
        super("log/" + BeeClientConfiguration.getLocalProperies().getAppName());
        addDConfigListener(this);
        startChildDataListen();
        startChildListListen();
    }

    @Override
    public void onConfigCreate(String key, String config) {
        onConfigUpdate(key, config);
    }

    @Override
    public void onConfigDelete(String key) {
        //
    }

    @Override
    public void onConfigUpdate(String key, String config) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        if (!StringUtil.isEmpty(config) && !StringUtil.isEmpty(key)) {
            ch.qos.logback.classic.Logger logger = loggerContext.getLogger(key);
            logger.setLevel(Level.toLevel(config, Level.INFO));
            TComLogs.debug("Logger {} chanage, pre level is {}", logger, logger.getLevel());
        }
        TComLogs.debug("log level chanage : {}, {}", key, config);
    }

    @Override
    public void onChildrenChanage(List<String> configKeyList) {
        //
    }
}
