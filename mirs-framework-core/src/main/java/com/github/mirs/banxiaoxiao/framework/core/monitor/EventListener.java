package com.github.mirs.banxiaoxiao.framework.core.monitor;


/**
 * @author zcy 2018年9月21日
 */
@Deprecated
public interface EventListener<E> {
    
    void onAppEvent(E event);
}
