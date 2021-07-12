package com.github.mirs.banxiaoxiao.framework.core.dcc.autoval.spring;

import com.github.mirs.banxiaoxiao.framework.common.util.ReflectionUtils;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zcy 2019年4月15日
 */
public class SpringBeanBinder {

    private Class<?> tagertClass;

    private Object targetBean;

    private Map<String, Method> setMethods = new HashMap<String, Method>();

    private Map<String, Object> vals = new HashMap<String, Object>();

    public SpringBeanBinder(Class<?> tagertClass) {
        this(tagertClass, null);
    }

    public SpringBeanBinder(Class<?> tagertClass, Object targetBean) {
        this.tagertClass = tagertClass;
        this.targetBean = targetBean;
        this.vals = pareseVals();
    }

    public Map<String, Object> getVals() {
        return vals;
    }

    protected Map<String, Object> pareseVals() {
        Map<String, Object> vals = new HashMap<String, Object>();
        pareseField(this.tagertClass, vals);
        pareseMethod(this.tagertClass, vals);
        return vals;
    }

    private void pareseField(Class<?> clazz, Map<String, Object> vals) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Value.class)) {
                Value springValue = field.getAnnotation(Value.class);
                String[] kv = pareseSpringValueExpression(springValue.value());
                vals.put(kv[0], kv[1]);
                String methodName = "set" + StringUtil.capitalize(field.getName());
                try {
                    Method method = ReflectionUtils.getMethod(clazz, methodName, String.class);
                    this.setMethods.put(kv[0], method);
                } catch (NoSuchMethodException | SecurityException e) {
                    try {
                        Method method = ReflectionUtils.getMethod(clazz, methodName, field.getType());
                        this.setMethods.put(kv[0], method);
                    } catch (NoSuchMethodException | SecurityException e1) {
                        TComLogs.warn("{} property {} set method not found", clazz.getName(), field.getName());
                    }
                }
            }
        }
    }

    private void pareseMethod(Class<?> clazz, Map<String, Object> vals) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Value.class)) {
                Value springValue = method.getAnnotation(Value.class);
                String[] kv = pareseSpringValueExpression(springValue.value());
                vals.put(kv[0], kv[1]);
                this.setMethods.put(kv[0], method);
            }
        }
    }

    /**
     * 解析spring @value 值，返回value配置key及其默认值
     *
     * @param valEx
     * @return retrunValue[0] 为key，retrunValue[1]为默认值
     */
    private String[] pareseSpringValueExpression(String valEx) {
        String[] kv = new String[2];
        int startIndex = valEx.indexOf("{");
        int endIndex = valEx.indexOf("}");
        if (startIndex < 0) {
            startIndex = 0;
        } else {
            startIndex += 1;
        }
        if (endIndex < 0) {
            endIndex = valEx.length();
        }
        String keyVal = valEx.substring(startIndex, endIndex);
        if (keyVal.contains(":")) {
            String[] array = keyVal.split(":");
            keyVal = array[0];
            if (array.length > 1) {
                kv[1] = array[1];
            } else {
                kv[1] = "";
            }
        } else {
            kv[1] = "";
        }
        kv[0] = keyVal;
        return kv;
    }

    public void bindSpringBean(Object bean) {
        this.targetBean = bean;
    }

    public void chanageVal(String key, Object value) {
        if (!this.vals.containsKey(key)) {
            return;
        }
        Method method = this.setMethods.get(key);
        if (method != null && this.targetBean != null) {
            try {
                ReflectionUtils.invokeMethod(method, this.targetBean, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                TComLogs.error("set val fail ", e);
            }
        }
    }
}
