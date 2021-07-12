package com.github.mirs.banxiaoxiao.framework.core.drpc;

/**
 * 动态group加载器，drpc在对外发布服务时实时获取group值
 * 
 * @author zcy 2019年3月21日
 */
public interface DServiceGroupLoader {

    /**
     * @param serviceBean
     * @return
     */
    public String getGroup();
    
}
