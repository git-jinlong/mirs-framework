package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq;

import com.github.mirs.banxiaoxiao.framework.common.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author erxiao 2017年2月5日
 * 订阅者的代理
 */
public class EventHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final Object target;

    private final Method method;

    public EventHandler(Object target, Method method) {
        this.target = target;
        this.method = method;
        method.setAccessible(true);
    }

    public void handle(String eventName, boolean isJsonType, String message, Object event) throws Exception {
        preHandle(event);
        try {
            if (isJsonType) {
                Class<?> clazz = Class.forName(eventName);
                Object object = JsonUtils.fromJson(clazz, message);
                method.invoke(target, new Object[]{object});
            } else {
                method.invoke(target, new Object[]{event});
            }
        } catch (IllegalArgumentException e) {
            throw new Error("Method rejected target/argument: " + event, e);
        } catch (IllegalAccessException e) {
            throw new Error("Method became inaccessible: " + event, e);
        } catch (Throwable e) {
            logger.error("handle object " + target + " method " + method + " error ", e);
            throw new Exception(e);
        } finally {
            afterHandle(event);
        }
    }

    protected void afterHandle(Object event) {
        // 后置
    }

    protected void preHandle(Object event) {
        // 前置
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        return (PRIME + method.hashCode()) * PRIME + target.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final EventHandler other = (EventHandler) obj;
        return method.equals(other.method) && target == other.target;
    }

    public String expression() {
        return target.getClass().getCanonicalName() + "#" + method.getName();
    }
}
