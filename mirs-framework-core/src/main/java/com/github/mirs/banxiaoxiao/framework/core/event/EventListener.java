package com.github.mirs.banxiaoxiao.framework.core.event;


/**
 * @author zcy 2018年9月21日
 */
public interface EventListener<E> {
    
    void onAppEvent(E event);
}
