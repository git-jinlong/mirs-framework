package com.github.mirs.banxiaoxiao.framework.core.dcc.autoval;

/**
 * @author zcy 2018年9月14日
 */
public interface Valistener {

    public void onChange(String namespace, String key, Object value);
    
}
