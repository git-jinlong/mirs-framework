package com.github.mirs.banxiaoxiao.framework.core.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mirs.banxiaoxiao.framework.common.util.ClassUtil;

/**
 * @author zcy 2018年9月25日
 */
public abstract class EventPublisher {

    private Logger logger = LoggerFactory.getLogger(EventPublisher.class);

    private Map<Class<?>, List<EventListener<?>>> listeners = new HashMap<Class<?>, List<EventListener<?>>>();
    
    private Map<Class<?>, List<BizInterceptor<?>>> interceptors = new HashMap<Class<?>, List<BizInterceptor<?>>>();

    @SuppressWarnings({ "rawtypes" })
    public <L> void notice(L event) {
        Class clazz = event.getClass();
        interceptor(NULL_LISTENER.class, event);
        do {
            interceptor(clazz, event);
            notice(clazz, event);
            clazz = clazz.getSuperclass();
        } while (!Object.class.equals(clazz));
        notice(NULL_LISTENER.class, event);
    }

    public <L> void notice(Class<?> clazz, L event) {
        List<EventListener<?>> eventListeners = this.listeners.get(clazz);
        notice(event, eventListeners);
    }

    @SuppressWarnings("unchecked")
    protected <L> void notice(L event, List<EventListener<?>> eventListeners) {
        if (eventListeners != null) {
            for (EventListener<?> temp : eventListeners) {
                EventListener<L> eventListener = (EventListener<L>) temp;
                try {
                    eventListener.onAppEvent(event);
                } catch (Throwable e) {
                    logger.error("notice event error ", e);
                }
            }
        }
    }
    
    protected <L> void interceptor(Class<?> clazz, L event) {
        List<BizInterceptor<?>> interceptors = this.interceptors.get(clazz);
        interceptor(event, interceptors);
    }

    @SuppressWarnings("unchecked")
    protected <L> void interceptor(L event, List<BizInterceptor<?>> interceptors) {
        if (interceptors != null) {
            for (BizInterceptor<?> temp : interceptors) {
                BizInterceptor<L> interceptor = (BizInterceptor<L>) temp;
                interceptor.interceptor(event);
            }
        }
    }

    public synchronized void addBizInterceptor(BizInterceptor<?> interceptor) {
        List<Class<?>> interceptorClasses = ClassUtil.getGenericClass(interceptor.getClass());
        if (interceptorClasses == null || interceptorClasses.size() == 0) {
            addBizInterceptor(NULL_LISTENER.class, interceptor);
        } else {
            for (Class<?> clazz : interceptorClasses) {
                addBizInterceptor(clazz, interceptor);
            }
        }
    }

    public synchronized void addBizInterceptor(Class<?> clazz, BizInterceptor<?> listener) {
        List<BizInterceptor<?>> interceptors = this.interceptors.get(clazz);
        if (interceptors == null) {
            interceptors = new ArrayList<BizInterceptor<?>>();
            this.interceptors.put(clazz, interceptors);
        }
        if (!interceptors.contains(listener)) {
            interceptors.add(listener);
        }
    }
    
    public synchronized void addEventListener(EventListener<?> listener) {
        List<Class<?>> eventClasses = ClassUtil.getGenericClass(listener.getClass());
        if (eventClasses == null || eventClasses.size() == 0) {
            addEventListener(NULL_LISTENER.class, listener);
        } else {
            for (Class<?> clazz : eventClasses) {
                addEventListener(clazz, listener);
            }
        }
    }
    
    public synchronized void addEventListener(Class<?> clazz, EventListener<?> listener) {
        List<EventListener<?>> eventListeners = this.listeners.get(clazz);
        if (eventListeners == null) {
            eventListeners = new ArrayList<EventListener<?>>();
            this.listeners.put(clazz, eventListeners);
        }
        if (!eventListeners.contains(listener)) {
            eventListeners.add(listener);
        }
    }

    static class NULL_LISTENER {
    }
}
