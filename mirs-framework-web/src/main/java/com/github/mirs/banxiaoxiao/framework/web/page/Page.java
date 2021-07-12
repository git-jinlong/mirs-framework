package com.github.mirs.banxiaoxiao.framework.web.page;

import java.lang.annotation.*;

/**
 * Annotation define some options for {@link PageParameter}.
 *
 * @author zw
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Page {

  /**
   * Defines all of fields that can be order by.
   *
   * @return fields can be order by
   */
  String[] allowOrderByFields();

  /**
   * Defines the default order by field.
   *
   * @return the default order by field
   */
  String orderBy() default "";


}
