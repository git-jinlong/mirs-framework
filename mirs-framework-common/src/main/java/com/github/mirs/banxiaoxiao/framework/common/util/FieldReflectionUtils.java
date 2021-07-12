package com.github.mirs.banxiaoxiao.framework.common.util;


import com.github.mirs.banxiaoxiao.framework.common.excel.annotation.ExportField;
import com.github.mirs.banxiaoxiao.framework.common.excel.annotation.ExportIgnore;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * 字段映射反射转换值
 *
 * @author bc
 */
public class FieldReflectionUtils {

    private FieldReflectionUtils() {
    }

    public static Byte parseByte(String value) {
        try {
            value = value.replaceAll("　", "");
            return Byte.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseByte but input illegal input=" + value, e);
        }
    }

    public static Boolean parseBoolean(String value) {
        value = value.replaceAll("　", "");
        if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        } else if (Boolean.FALSE.toString().equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        } else {
            throw new RuntimeException("parseBoolean but input illegal input=" + value);
        }
    }

    public static Integer parseInt(String value) {
        try {
            value = value.replaceAll("　", "");
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseInt but input illegal input=" + value, e);
        }
    }

    public static Short parseShort(String value) {
        try {
            value = value.replaceAll("　", "");
            return Short.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseShort but input illegal input=" + value, e);
        }
    }

    public static Long parseLong(String value) {
        try {
            value = value.replaceAll("　", "");
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseLong but input illegal input=" + value, e);
        }
    }

    public static Float parseFloat(String value) {
        try {
            value = value.replaceAll("　", "");
            return Float.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseFloat but input illegal input=" + value, e);
        }
    }

    public static Double parseDouble(String value) {
        try {
            value = value.replaceAll("　", "");
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseDouble but input illegal input=" + value, e);
        }
    }

    public static Date parseDate(String value, ExportField excelField) {
        try {

            String datePattern = "yyyy-MM-dd HH:mm:ss";

            if (StringUtils.isEmpty(value)) {
                return null;
            }

            if (excelField != null) {
                datePattern = excelField.dateFormat();
                if (value.contains("/")) {
                    datePattern = "yyyy/M/dd HH:mm";
                }
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
            return dateFormat.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException("parseDate but input illegal input=" + value, e);
        }
    }

    /**
     * 参数解析 （支持：Byte、Boolean、String、Short、Integer、Long、Float、Double、Date）
     */
    public static Object parseValue(Field field, String value) {
        Class<?> fieldType = field.getType();

        ExportField excelField = field.getAnnotation(ExportField.class);
        ExportIgnore ignore = field.getAnnotation(ExportIgnore.class);
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        if (null != ignore) {
            return null;
        }
        value = value.trim();

		/*if (Byte.class.equals(fieldType) || Byte.TYPE.equals(fieldType)) {
			return parseByte(value);
		} else */
        if (Boolean.class.equals(fieldType) || Boolean.TYPE.equals(fieldType)) {
            return parseBoolean(value);
        }/* else if (Character.class.equals(fieldType) || Character.TYPE.equals(fieldType)) {
			 return value.toCharArray()[0];
		}*/ else if (String.class.equals(fieldType)) {
            return value;
        } else if (Short.class.equals(fieldType) || Short.TYPE.equals(fieldType)) {
            return parseShort(value);
        } else if (Integer.class.equals(fieldType) || Integer.TYPE.equals(fieldType)) {
            return parseInt(value);
        } else if (Long.class.equals(fieldType) || Long.TYPE.equals(fieldType)) {
            return parseLong(value);
        } else if (Float.class.equals(fieldType) || Float.TYPE.equals(fieldType)) {
            return parseFloat(value);
        } else if (Double.class.equals(fieldType) || Double.TYPE.equals(fieldType)) {
            return parseDouble(value);
        } else if (Date.class.equals(fieldType)) {
            return parseDate(value, excelField);

        } else {
            throw new RuntimeException(
                    "request illeagal type, type must be Integer not int Long not long etc, type="
                            + fieldType);
        }
    }

    public static byte[] formatBytes(Field field, Object value) {

        return (byte[]) value;
    }

    /**
     * 参数格式化为String
     */
    public static String formatValue(Field field, Object value) {
        Class<?> fieldType = field.getType();
        if (value == null) {
            return null;
        }
        ExportField excelField = field.getAnnotation(ExportField.class);

        if (Boolean.class.equals(fieldType) || Boolean.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (String.class.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Short.class.equals(fieldType) || Short.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Integer.class.equals(fieldType) || Integer.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Long.class.equals(fieldType) || Long.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Float.class.equals(fieldType) || Float.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Double.class.equals(fieldType) || Double.TYPE.equals(fieldType)) {
            return String.valueOf(value);
        } else if (Date.class.equals(fieldType)) {
            String datePattern = "yyyy-MM-dd HH:mm:ss";
            if (excelField != null && excelField.dateFormat() != null) {
                datePattern = excelField.dateFormat();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
            return dateFormat.format(value);
        } else if (LocalDateTime.class.equals(fieldType)) {
            String localDateTimePattern = "yyyy-MM-dd HH:mm:ss";
            if (excelField != null) {
                excelField.dateFormat();
                localDateTimePattern = excelField.dateFormat();
            }
            DateTimeFormatter df = DateTimeFormatter.ofPattern(localDateTimePattern);
            return df.format((TemporalAccessor) value);
        } else {
            throw new RuntimeException("request illeagal type, type must be Integer not int Long not long etc, type=" + fieldType);
        }
    }
}
