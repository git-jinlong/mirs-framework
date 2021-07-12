package com.github.mirs.banxiaoxiao.framework.common.excel.annotation;

import java.lang.annotation.*;

/**
 * 导出忽略信息
 * @author bc
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExportIgnore {

}
