package com.github.mirs.banxiaoxiao.framework.core.annotation;

import java.lang.annotation.*;

/**
 * Indicates you shouldn't do block operations when implementation these methods.
 *
 * @author zw
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DoNotBlocking {

}
