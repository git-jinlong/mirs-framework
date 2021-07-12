package com.github.mirs.banxiaoxiao.framework.core.drpc.p2p;

import com.github.mirs.banxiaoxiao.framework.core.dcc.client.ClientId;
import com.github.mirs.banxiaoxiao.framework.core.drpc.DServiceGroupLoader;

/**
 * @author zcy 2019年3月26日
 */
public class ClientIdGroupLoader implements DServiceGroupLoader {

    @Override
    public String getGroup() {
        return ClientId.get();
    }
}
