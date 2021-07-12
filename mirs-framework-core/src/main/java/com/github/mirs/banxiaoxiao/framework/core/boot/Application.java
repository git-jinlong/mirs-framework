/**
 *
 */
package com.github.mirs.banxiaoxiao.framework.core.boot;

import java.lang.annotation.*;

/**
 * @author erxiao 2017年1月18日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Application {

    String value();

    String[] scanPackage() default {};

}
