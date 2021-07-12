package com.github.mirs.banxiaoxiao.framework.common.excel.annotation;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.lang.annotation.*;

/**
 * 导出数据信息的字段信息
 * <pre>
 *   1.支持java的对象数据类型，Boolean、String、Short、Integer、Long、Float、Double、Date
 *   2.支持excel、csv的数据类型为string
 * </pre>
 *
 * @author bc
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExportField {

    /**
     * 列名称
     */
    String name() default "";

    /**
     * 列宽 （大于0生效，默认自适应）
     */
    int width() default 0;

    /**
     * 是否添加文件锚点
     *
     * @return boolean
     */
    boolean isLink() default false;

    /**
     * 是否插入图片
     *
     * @return True or False
     */
    boolean isImage() default false;

    /**
     * 单元格内容颜色
     */
    IndexedColors color() default IndexedColors.GREEN;

    /**
     * 水平对齐方式
     */
    HorizontalAlignment align() default HorizontalAlignment.LEFT;

    /**
     * 时间格式化，日期类型时生效
     */
    String dateFormat() default "yyyy-MM-dd HH:mm:ss";

}
