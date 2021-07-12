package com.github.mirs.banxiaoxiao.framework.core.annotation;

import java.lang.annotation.*;

/**
 * Indicates the class is not thread-safe hence you must not used in concurrent.
 *
 * @author zw
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ThreadNotSafe {

}
