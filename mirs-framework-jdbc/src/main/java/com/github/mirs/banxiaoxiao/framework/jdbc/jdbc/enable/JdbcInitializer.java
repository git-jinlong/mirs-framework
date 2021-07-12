package com.github.mirs.banxiaoxiao.framework.jdbc.jdbc.enable;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.boot.ApplicationInitializer;
import com.github.mirs.banxiaoxiao.framework.core.boot.InitializeException;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.jdbc.jdbc.CommonDataSouceAutoConf;
import com.github.mirs.banxiaoxiao.framework.jdbc.jdbc.ShardingDataSouceAutoConf;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author zcy 2019年5月21日
 */
public class JdbcInitializer implements ApplicationInitializer {

    @Override
    public void init(ConfigurableApplicationContext applicationContext) throws InitializeException {
        String jdbcType = applicationContext.getEnvironment().getProperty("bee.jdbc.type");
        TComLogs.info("init jdbc config, jdbc type is {} ", jdbcType);
        if (StringUtil.equalsIgnoreCase(jdbcType, "sharding")) {
            registerBean(ShardingDataSouceAutoConf.class, applicationContext);
        } else {
            registerBean(CommonDataSouceAutoConf.class, applicationContext);
        }

    }

}
