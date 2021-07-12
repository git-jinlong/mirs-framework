package com.github.mirs.banxiaoxiao.framework.common.util;

import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * java 8 简单的stream 转换
 *
 * @author bc
 */
public final class StreamUtils {

    @SafeVarargs
    public static <T, R> List<R> transListFromMappers(List<T> list, Function<T, R>... mappers) {

        Stream<T> stream = list.stream();
        Stream<R> rStream = Stream.empty();

        for (Function<T, R> function : mappers) {
            rStream = stream.map(function);
        }

        return rStream.collect(Collectors.toList());

    }

    @SafeVarargs
    public static <T> List<T> transListFromFilters(List<T> list, Predicate<T>... filters) {

        Stream<T> stream = list.stream();

        for (Predicate<T> predicate : filters) {
            stream = stream.filter(predicate);
        }

        return stream.collect(Collectors.toList());
    }

    public static <T, R> Map<R, T> transListToMap(List<T> list, Function<T, R> keyMapper,
                                                  Function<T, T> valueMapper) {

        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }

        return list.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * 简单将list转换成map
     */
    public static <T, R, U> Map<R, U> convertListToMap(List<T> list, Function<T, R> keyMapper, Function<T, U> valueMapper) {

        return list.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * 流式分页
     *
     * @param list
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public static <T> List<T> pageHelper(List<T> list, int pageNumber, int pageSize) {
        return list.stream().skip((pageNumber - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
    }
}

