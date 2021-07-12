package com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.intercept.comparator;

import com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.intercept.BeeIbatisIntercept;
import com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.intercept.BeeJdbcIbatisInterceptException;
import com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.intercept.annotation.Order;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.OrderUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 排序
 *
 * @author lxj
 * @date 2020-07-07
 * @see Order
 */
public class AnnotationAwareInterceptOrderComparator extends InterceptOrderComparator {

    /**
     * Shared default instance of {@code AnnotationAwareOrderComparator}.
     */
    public static final AnnotationAwareInterceptOrderComparator INSTANCE = new AnnotationAwareInterceptOrderComparator();


    /**
     * This implementation checks for {@link Order @Order}
     * elements, in addition to the {@link org.springframework.core.Ordered}
     * check in the superclass.
     */
    @Override
    protected Integer findOrder(Object obj) {
        // Check for regular Ordered interface
        Integer order = super.findOrder(obj);
        if (order != null) {
            return order;
        }

        // Check for @Order and @Priority on various kinds of elements
        if (obj instanceof Class) {
            return OrderUtils.getOrder((Class<?>) obj);
        } else if (obj instanceof Method) {
            Order ann = AnnotationUtils.findAnnotation((Method) obj, Order.class);
            if (ann != null) {
                return ann.value();
            }
        } else if (obj instanceof AnnotatedElement) {
            Order ann = AnnotationUtils.getAnnotation((AnnotatedElement) obj, Order.class);
            if (ann != null) {
                return ann.value();
            }
        } else if (obj != null) {
            Order ann = AnnotationUtils.findAnnotation(obj.getClass(), Order.class);
            if (ann != null) {
                return ann.value();
            }
        }

        return order;
    }

    /**
     * check oder
     */
    public static boolean haveOrder(Object obj) {
        Integer order = INSTANCE.findOrder(obj);
        if (Objects.nonNull(order)) {
            if (order <= 0 && !(obj instanceof BeeIbatisIntercept)) {
                throw new BeeJdbcIbatisInterceptException("IbatisIntercept annotation @Order ,value must greater than 0 ,please check.");
            }
        }
        return Objects.nonNull(order);
    }


    /**
     * Sort the given List with a default AnnotationAwareOrderComparator.
     * <p>Optimized to skip sorting for lists with size 0 or 1,
     * in order to avoid unnecessary array extraction.
     *
     * @param list the List to sort
     * @see Collections#sort(List, java.util.Comparator)
     */
    public static void sort(List<?> list) {
        if (list.size() > 1) {
            Collections.sort(list, INSTANCE);
        }
    }

    /**
     * Sort the given array with a default AnnotationAwareOrderComparator.
     * <p>Optimized to skip sorting for lists with size 0 or 1,
     * in order to avoid unnecessary array extraction.
     *
     * @param array the array to sort
     * @see Arrays#sort(Object[], java.util.Comparator)
     */
    public static void sort(Object[] array) {
        if (array.length > 1) {
            Arrays.sort(array, INSTANCE);
        }
    }

    /**
     * Sort the given array or List with a default AnnotationAwareOrderComparator,
     * if necessary. Simply skips sorting when given any other value.
     * <p>Optimized to skip sorting for lists with size 0 or 1,
     * in order to avoid unnecessary array extraction.
     *
     * @param value the array or List to sort
     * @see Arrays#sort(Object[], java.util.Comparator)
     */
    public static void sortIfNecessary(Object value) {
        if (value instanceof Object[]) {
            sort((Object[]) value);
        } else if (value instanceof List) {
            sort((List<?>) value);
        }
    }

}
