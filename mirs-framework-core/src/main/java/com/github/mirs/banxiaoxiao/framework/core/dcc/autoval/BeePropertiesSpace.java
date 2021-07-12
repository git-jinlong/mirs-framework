package com.github.mirs.banxiaoxiao.framework.core.dcc.autoval;

import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;


public class BeePropertiesSpace extends BaseDccValSpace {
    
    /***/
    private static final long serialVersionUID = -3326196929615264870L;
    
    public BeePropertiesSpace(DccClient dccClient) {
        super(dccClient);
    }

    public int getPriority() {
        return 300;
    }

    @Override
    public String getNamespace() {
        return "bee";
    }

}
