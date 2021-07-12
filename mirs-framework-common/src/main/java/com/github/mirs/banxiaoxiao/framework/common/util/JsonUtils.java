package com.github.mirs.banxiaoxiao.framework.common.util;

import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * @author erxiao 2016年2月26日
 */
public class JsonUtils {

    /**
     * 将对象转换成json字符串，如果对象为null，返回{}
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return new GsonBuilder().create().toJson(obj);
        }
    }

    public static String toJson(Object obj, JsonTypeAdapter... adapters) {
        if (obj == null) {
            return "";
        } else {
            GsonBuilder builder = new GsonBuilder();
            if (adapters != null) {
                for (JsonTypeAdapter adapter : adapters) {
                    builder.registerTypeAdapter(adapter.getAdapterType(), adapter);
                }
            }
            return builder.create().toJson(obj);
        }
    }

    /**
     * 将json字符串转换成对象，如果json格式不正确，或json为空，则返回null
     *
     * @param clazz
     * @param json
     * @param adapters
     * @return
     */
    public static <T> T fromJson(Class<T> clazz, String json, JsonTypeAdapter... adapters) {
        try {
            GsonBuilder builder = new GsonBuilder();
            if (adapters != null) {
                for (JsonTypeAdapter adapter : adapters) {
                    builder.registerTypeAdapter(adapter.getAdapterType(), adapter);
                }
            }
            return builder.create().fromJson(json, clazz);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 将json字符串转换成对象，如果json格式不正确，或json为空，则返回null
     *
     * @param type
     * @param json
     * @param adapters
     * @return
     */
    public static <T> T fromJson(Type type, String json, JsonTypeAdapter... adapters) {
        try {
            GsonBuilder builder = new GsonBuilder();
            if (adapters != null) {
                for (JsonTypeAdapter adapter : adapters) {
                    builder.registerTypeAdapter(adapter.getAdapterType(), adapter);
                }
            }
            return builder.create().fromJson(json, type);
        } catch (Throwable e) {
            return null;
        }
    }
}