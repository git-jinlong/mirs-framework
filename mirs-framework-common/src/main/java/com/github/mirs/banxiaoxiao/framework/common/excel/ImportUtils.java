package com.github.mirs.banxiaoxiao.framework.common.excel;


import com.csvreader.CsvReader;
import com.github.mirs.banxiaoxiao.framework.common.excel.annotation.ExcelSheet;
import com.github.mirs.banxiaoxiao.framework.common.excel.annotation.ExportField;
import com.github.mirs.banxiaoxiao.framework.common.excel.annotation.ExportIgnore;
import com.github.mirs.banxiaoxiao.framework.common.util.FieldReflectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * excel 导入工具类
 *
 * @author bc
 */
@Slf4j
public class ImportUtils {

    /**
     * 从Workbook导入Excel文件，并封装成对象
     */
    public static <T> List<T> importExcel(Workbook workbook, Class<T> clazz) {
        List<T> sheetDataList = importSheet(workbook, clazz);
        return sheetDataList;
    }

    public static <T> List<T> importSheet(Workbook workbook, Class<T> clazz) {
        try {
            // sheet
            ExcelSheet excelSheet = clazz.getAnnotation(ExcelSheet.class);
            if (null == excelSheet) {
                return Collections.emptyList();
            }

            String sheetName = excelSheet.name();

            if (!StringUtils.isEmpty(sheetName) && sheetName.trim().length() > 0) {
                sheetName = sheetName.trim();
            } else {
                sheetName = clazz.getSimpleName();
            }

            // sheet field
            List<Field> fieldList = getFieldList(clazz);

            // sheet data
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                return Collections.emptyList();
            }

            Iterator<Row> sheetIterator = sheet.rowIterator();
            int rowIndex = 0;
            List<T> dataList = Lists.newArrayList();
            while (sheetIterator.hasNext()) {
                Row rowX = sheetIterator.next();
                if (rowIndex > 0) {
                    Object rowObj = clazz.newInstance();
                    for (int i = 0; i < fieldList.size(); i++) {

                        // cell
                        Cell cell = rowX.getCell(i);
                        if (cell == null) {
                            continue;
                        }

                        // call val str
                        // begin: fixed bug http://jira.arcvideo.com:8080/browse/FACE-515
                        // this line will clean value
//            cell.setCellType(CellType.STRING);

                        // ended
                        //String fieldValueStr = cell.getStringCellValue();       // cell.getCellTypeEnum()
                        /**
                         * 之前写法是只认识String类型数据，一旦获取到其他类型就会有异常
                         * 兼容Excel各种类型，用String处理
                         */
                        String fieldValueStr = getCellValue(cell);

                        // java val
                        Field field = fieldList.get(i);
                        Object fieldValue = FieldReflectionUtils.parseValue(field, fieldValueStr);
                        if (fieldValue == null) {
                            continue;
                        }

                        // fill val
                        field.setAccessible(true);
                        field.set(rowObj, fieldValue);
                    }
                    dataList.add((T) rowObj);
                }
                rowIndex++;
            }
            return dataList;
        } catch (IllegalAccessException | InstantiationException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static String getCellValue(Cell cell) {
        String value;

        DecimalFormat df = new DecimalFormat("0");//格式化number String字符串
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//日期格式化

        switch (cell.getCellTypeEnum()) {
            case STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case NUMERIC:
                if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                    value = df.format(cell.getNumericCellValue());
                } else if ("m/d/yy".equals(cell.getCellStyle().getDataFormatString())) {
                    value = sdf.format(cell.getDateCellValue());
                } else {
                    value = df.format(cell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                value = String.valueOf(cell.getBooleanCellValue());
                break;
            case BLANK:
                value = "";
                break;
            default:
                value = cell.toString();
                break;
        }
        return value;
    }

    /**
     * 导入Excel文件，并封装成对象
     */
    public static <T> List<T> importExcel(File excelFile, Class<T> clazz) {
        try {
            Workbook workbook = WorkbookFactory.create(excelFile);
            List<T> dataList = importExcel(workbook, clazz);
            return dataList;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从文件路径导入Excel文件，并封装成对象
     */
    public static <T> List<T> importExcel(String filePath, Class<T> sheetClass) {
        File excelFile = new File(filePath);
        List<T> dataList = importExcel(excelFile, sheetClass);
        return dataList;
    }

    /**
     * 导入Excel数据流，并封装成对象
     */
    public static <T> List<T> importExcel(InputStream inputStream, Class<T> sheetClass) {
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            List<T> dataList = importExcel(workbook, sheetClass);
            return dataList;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取实体类的导出字段名和实体的字段映射
     *
     * @param clazz model class
     * @param <T>   泛型
     * @return map
     */
    private static <T> Map<String, String> getFieldMap(Class<T> clazz) {
        List<Field> fieldList = getFieldList(clazz);
        Map<String, String> filedMap = Maps.newHashMap();
        for (int idx = 0; idx < fieldList.size(); idx++) {
            Field field = fieldList.get(idx);
            ExportIgnore ignore = field.getAnnotation(ExportIgnore.class);
            if (null != ignore) {
                //忽略有导入忽略的字段
                continue;
            }
            ExportField exportField = field.getAnnotation(ExportField.class);
            if (null == exportField) {
                continue;
            }
            field.setAccessible(true);
            filedMap.put(exportField.name(), field.getName());
        }
        return filedMap;
    }


    /**
     * import csv from file path
     *
     * @param file  csv file
     * @param clazz java bean class
     */
    public static <T> List<T> importCsv(File file, Class<T> clazz) {
        return importCsv(file.getAbsolutePath(), clazz);
    }

    /**
     * import csv from file path
     *
     * @param filePath csv file path
     * @param clazz    java bean class
     */
    public static <T> List<T> importCsv(String filePath, Class<T> clazz) {

        List<T> retList = Lists.newArrayList();

        // 创建CSV读对象
        CsvReader csvReader = null;
        try {
            csvReader = new CsvReader(filePath, ',', Charset.forName("utf-8"));
            // 所有成员变量
            Field[] fields = clazz.getDeclaredFields();

            fields = filterIgnore(fields);

            // 成员变量的值
            Object value = "";
            //读取csv文件列标题
            csvReader.readHeaders();
            while (csvReader.readRecord()) {
                Object newInstance = clazz.newInstance();
                // 读一整行
                csvReader.getRawRecord();
                for (int f = 0; f < fields.length; f++) {
                    fields[f].setAccessible(true);
                    String fieldName = fields[f].getName();
                    String tempVal = csvReader.get(f);
                    if (StringUtils.isEmpty(tempVal)) {
                        continue;
                    }
                    value = FieldReflectionUtils.parseValue(fields[f], tempVal);
                    // 赋值
                    PropertyUtils.setProperty(newInstance, fieldName, value);
                }
                retList.add((T) newInstance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != csvReader) {
                csvReader.close();
            }
        }
        return retList;
    }

    /**
     * Return fields which is filtered ignore fields has annotation {@link ExportIgnore}.
     *
     * @param fields original field array
     * @return new field array filtered ignore fields
     */
    public static Field[] filterIgnore(Field[] fields) {
        for (Field field : fields) {
            if (field.isAnnotationPresent(ExportIgnore.class)) {
                fields = ArrayUtils.removeElement(fields, field);
            }
        }
        return fields;
    }
//  /**
//   * import csv from file path
//   *
//   * @param filePath csv file path
//   * @param clazz java bean class
//   */
//  public static <T> List<T> importCsv(String filePath, Class<T> clazz) {
//
//    List<T> retList = Lists.newArrayList();
//
//    Map<String, String> fieldMap = getFieldMap(clazz);
//
//    List<Field> fieldList = getFieldList(clazz);
//    // 创建CSV读对象
//    CsvReader csvReader = null;
//    try {
//      csvReader = new CsvReader(filePath, ',', Charset.forName("utf-8"));
//      // 所有成员变量
//      Field[] fields = clazz.getDeclaredFields();
//      // 成员变量的值
//      Object value = "";
//      //读取csv文件列标题
//      csvReader.readHeaders();
//      while (csvReader.readRecord()) {
//        Object newInstance = clazz.newInstance();
//        // 读一整行
//        csvReader.getRawRecord();
//        for (int f = 0; f < fieldList.size(); f++) {
//          Field field = fieldList.get(f);
//          field.setAccessible(true);
//          String fieldName = field.getName();
//          if (!fieldMap.containsValue(fieldName)) {
//            continue;
//          }
//          String tempVal = csvReader.get(f);
//          if (StringUtils.isEmpty(tempVal)) {
//            continue;
//          }
//          value = FieldReflectionUtils.parseValue(field, tempVal);
//          // 赋值
//          PropertyUtils.setProperty(newInstance, fieldName, value);
//        }
//        retList.add((T) newInstance);
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//    } finally {
//      if (null != csvReader) {
//        csvReader.close();
//      }
//    }
//    return retList;
//  }

    /**
     * 获取从class 获取字段
     */
    private static List<Field> getFieldList(Class<?> clazz) {
        // sheet field
        List<Field> fieldList = Lists.newArrayList();
        if (clazz.getDeclaredFields() != null && clazz.getDeclaredFields().length > 0) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                ExportIgnore ignore = field.getAnnotation(ExportIgnore.class);
                if (null != ignore) {
                    continue;
                }
                ExportField exportField = field.getAnnotation(ExportField.class);
                if (null == exportField) {
                    continue;
                }
                fieldList.add(field);
            }
        }

        if (CollectionUtils.isEmpty(fieldList)) {
            return Collections.emptyList();
        }
        return fieldList;
    }

}
