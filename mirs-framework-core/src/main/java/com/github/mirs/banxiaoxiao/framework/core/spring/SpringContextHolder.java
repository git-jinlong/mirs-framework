package com.github.mirs.banxiaoxiao.framework.core.spring;

import org.springframework.context.ApplicationContext;

/**
 * @author zcy 2019年6月4日
 */
public class SpringContextHolder {

    private static ApplicationContext context;

    public static void set(ApplicationContext c) {
        context = c;
    }

    public static ApplicationContext get() {
        return context;
    }
}
