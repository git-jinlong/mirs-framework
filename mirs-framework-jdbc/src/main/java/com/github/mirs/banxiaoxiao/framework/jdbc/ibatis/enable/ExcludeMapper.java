package com.github.mirs.banxiaoxiao.framework.jdbc.ibatis.enable;

import java.lang.annotation.*;

/**
 * 排除在外的@Mapper，默认不自动注入
 * 
 * @author zcy 2019年11月15日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcludeMapper {

    String[] exclude() default {};
}
