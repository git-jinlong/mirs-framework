package com.github.mirs.banxiaoxiao.framework.common.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据类型Map
 * 
 * @author zcy 2019年4月29日
 */
public class MultiTypeValMap implements Serializable {

    /** */
    private static final long serialVersionUID = -2637533209470073876L;

    private Map<String, Object> vals = new HashMap<String, Object>();

    public void put(String key, Object val) {
        if (this.vals == null) {
            this.vals = new HashMap<String, Object>();
        }
        this.vals.put(key, val);
    }

    public void putAll(Map<String, Object> vals) {
        if (this.vals == null) {
            this.vals = new HashMap<String, Object>();
        }
        this.vals.putAll(vals);
    }

    public Map<String, Object> getVals() {
        return this.vals;
    }

    /**
     * 读取String型的属性，如果属性不存在或为null，则返回空字符串 “”
     * 
     * @param key
     * @return
     */
    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, String def) {
        return get(String.class, key, def);
    }

    public Integer getInt(String key) {
        return getInt(key, 0);
    }

    public Integer getInt(String key, Integer def) {
        return get(Integer.class, key, def);
    }

    public Long getLong(String key) {
        return getLong(key, 0L);
    }

    public Long getLong(String key, Long def) {
        return get(Long.class, key, def);
    }

    public Float getFloat(String key) {
        return getFloat(key, 0F);
    }

    public Float getFloat(String key, Float def) {
        return get(Float.class, key, def);
    }

    public Double getDouble(String key) {
        return getDouble(key, 0D);
    }

    public Double getDouble(String key, Double def) {
        return get(Double.class, key, def);
    }

    public Short getShort(String key) {
        return getShort(key, (short) 0);
    }

    public Short getShort(String key, Short def) {
        return get(Short.class, key, def);
    }

    /**
     * 如果field不存在，取默认值为false
     * 
     * @param key
     * @return
     */
    public Boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public Boolean getBoolean(String key, Boolean def) {
        return get(Boolean.class, key, def);
    }

    public <T> T get(Class<T> baseClazz, String key, T def) {
        Map<String, Object> fieldMap = getVals();
        if (fieldMap == null || !fieldMap.containsKey(key)) {
            return def;
        } else {
            Object val = fieldMap.get(key);
            if (val == null) {
                return def;
            } else {
                return convert(baseClazz, val.toString(), def);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T convert(Class<T> baseClazz, String val, T def) {
        try {
            if (baseClazz == String.class) {
                return (T) val;
            } else if (baseClazz == Integer.class) {
                return (T) new Integer(val.toString());
            } else if (baseClazz == Long.class) {
                return (T) new Long(val.toString());
            } else if (baseClazz == Float.class) {
                return (T) new Float(val.toString());
            } else if (baseClazz == Double.class) {
                return (T) new Double(val.toString());
            } else {
                return JsonUtils.fromJson(baseClazz, val);
            }
        } catch (Exception e) {
            return def;
        }
    }
}
