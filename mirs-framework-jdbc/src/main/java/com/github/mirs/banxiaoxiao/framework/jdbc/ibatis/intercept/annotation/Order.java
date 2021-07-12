package com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.intercept.annotation;

import com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.intercept.BeeJdbcIbatisInterceptException;
import org.apache.ibatis.plugin.InterceptorChain;

import java.lang.annotation.*;

/**
 * <li>mybatis Intercept原生执行是遍历List，唯一保证顺序的是放入的先后顺序，这里进行简单保证插入顺序来维护Intercept执行顺序,参考：{@link InterceptorChain}。</li>
 * <li>有此注解的Intercept 要优先于无此注解的拦截执行</li>
 *
 * @auther lxj
 * @date 2020/7/7 10:59
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Order {

    /**
     * 排序依据：越小越优先执行,外部使用约定为正整数否则报错
     * {@link BeeJdbcIbatisInterceptException}
     */
    int value() default 0;
}
