package com.github.mirs.banxiaoxiao.framework.core.dcc.autoval;

import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;

/**
 * @author zcy 2018年9月14日
 */
public class AppDefaultSpace extends BaseDccValSpace {

    /** */
    private static final long serialVersionUID = 3596562018937431174L;

    public AppDefaultSpace(DccClient dccClient) {
        super(dccClient);
    }

    public int getPriority() {
        return 100;
    }
    
    @Override
    public String getNamespace() {
        return BeeClientConfiguration.getLocalProperies().getAppName();
    }
    
    
    public static String namespace() {
        return BeeClientConfiguration.getLocalProperies().getAppName();
    }
}
