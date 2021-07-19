package com.github.mirs.banxiaoxiao.framework.dtask.seres;

import java.util.List;

/**
 * @author zcy 2019年5月29日
 */
public interface ServerResourceDiscovery {

    List<ServerResource> discovery();
    
    void setAppnames(List<String> appnames);
}
