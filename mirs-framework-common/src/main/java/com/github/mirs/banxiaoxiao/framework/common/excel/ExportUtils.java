package com.github.mirs.banxiaoxiao.framework.common.excel;

import com.csvreader.CsvWriter;
import com.github.mirs.banxiaoxiao.framework.common.excel.annotation.ExcelSheet;
import com.github.mirs.banxiaoxiao.framework.common.excel.annotation.ExportField;
import com.github.mirs.banxiaoxiao.framework.common.excel.annotation.ExportIgnore;
import com.github.mirs.banxiaoxiao.framework.common.util.FieldReflectionUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.xssf.streaming.SXSSFCreationHelper;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * @author bc
 */
@Slf4j
public class ExportUtils {

    private static final String SPRIT = "/";

    /**
     * 导出excel
     * <pre>
     *   1.传入的数据实体List<?>里面如果ExportField字段面isLink = true
     *   ，则需要把list中的数据转换为当前工作目录下面的相对路径
     *   2.不需要为文件添加锚点的话，isLink不用设置，默认对整个表格不添加锚点
     * </pre>
     *
     * @param fileFolder 父文件夹
     * @param fileName   文件名
     * @param dataList   数据集合
     */
    public static <T> void exportExcel(String fileFolder, String fileName, List<T> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            log.warn("exportExcel error, data array can not be empty.");
            return;
        }

        File file = new File(fileFolder);
        if (!file.exists()) {
            file.mkdirs();
        }

        String filePath = fileFolder.concat(SPRIT).concat(fileName);

        OutputStream outs = null;
        try {
            outs = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            log.error("exportExcel file not found, ", e);
            return;
        }

        //keep 100 rows in memory, exceeding rows will be flushed to disk
        SXSSFWorkbook wb = new SXSSFWorkbook(10000);

        makeExcelSheet(wb, dataList);

        try {
            wb.write(outs);
        } catch (IOException e) {
            log.error("export excel error,", e);
        } finally {
            try {
                outs.flush();
                outs.close();
            } catch (IOException e) {
                log.error("exportExcel close error", e);
            }
            try {
                wb.close();
            } catch (IOException e) {
                log.error("close wb error,", e);
            }

        }
    }

    /**
     * 生成excel 数据
     *
     * @param workbook      Workbook
     * @param sheetDataList 数据集合
     */
    private static <T> void makeExcelSheet(SXSSFWorkbook workbook, List<T> sheetDataList) {
        // data
        if (sheetDataList == null || sheetDataList.size() == 0) {
            throw new RuntimeException("make excel error, data can not be empty.");
        }
        SXSSFCreationHelper helper = (SXSSFCreationHelper) workbook.getCreationHelper();

        // sheet
        Class<?> sheetClass = sheetDataList.get(0).getClass();
        ExcelSheet excelSheet = sheetClass.getAnnotation(ExcelSheet.class);

        String sheetName = sheetDataList.get(0).getClass().getSimpleName();
        int headColorIndex = -1;
        if (excelSheet != null) {
            if (excelSheet.name().trim().length() > 0) {
                sheetName = excelSheet.name().trim();
            }
            headColorIndex = excelSheet.headColor().getIndex();
        }

        SXSSFSheet existSheet = workbook.getSheet(sheetName);
        if (existSheet != null) {
            for (int i = 2; i <= 1000; i++) {
                String newSheetName = sheetName.concat(String.valueOf(i));  // avoid sheetName repetition
                existSheet = workbook.getSheet(newSheetName);
                if (existSheet == null) {
                    sheetName = newSheetName;
                    break;
                }
            }
        }

        SXSSFSheet sheet = workbook.createSheet(sheetName);
        //sheet画图管理工具, 每个sheet维持一个
        SXSSFDrawing patriarch = sheet.createDrawingPatriarch();
        // sheet field
        List<Field> fields = Lists.newArrayList();
        if (sheetClass.getDeclaredFields().length > 0) {
            for (Field field : sheetClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                //忽略有导出注解的字段信息
                if (field.getAnnotation(ExportIgnore.class) != null) {
                    continue;
                }
                fields.add(field);
            }
        }

        if (CollectionUtils.isEmpty(fields)) {
            log.warn("makeExcelSheet error, data field can not be empty.");
            return;
        }

        // sheet header row
        CellStyle[] fieldDataStyleArr = new CellStyle[fields.size()];
        int[] fieldWidthArr = new int[fields.size()];
        boolean[] linkArr = new boolean[fields.size()];
        boolean[] imageArr = new boolean[fields.size()];
        IndexedColors[] cellColors = new IndexedColors[fields.size()];
        Row headRow = sheet.createRow(0);
        for (int i = 0; i < fields.size(); i++) {

            // field
            Field field = fields.get(i);
            ExportField excelField = field.getAnnotation(ExportField.class);

            String fieldName = field.getName();
            int fieldWidth = 0;
            HorizontalAlignment align = null;
            boolean isLink = false;
            boolean isImage = false;
            IndexedColors green = IndexedColors.GREEN;
            if (excelField != null) {
                if (!StringUtils.isEmpty(excelField.name()) && excelField.name().trim().length() > 0) {
                    fieldName = excelField.name().trim();
                }
                fieldWidth = excelField.width();
                align = excelField.align();
                isLink = excelField.isLink();
                green = excelField.color();
                isImage = excelField.isImage();
            }

            //是否添加锚点集合

            linkArr[i] = isLink;
            imageArr[i] = isImage;
            cellColors[i] = green;
            // field width
            fieldWidthArr[i] = fieldWidth;

            // head-style、field-data-style
            CellStyle fieldDataStyle = workbook.createCellStyle();
            if (align != null) {
                fieldDataStyle.setAlignment(align);
            }
            if (green != IndexedColors.GREEN) {
                fieldDataStyle.setFillForegroundColor(green.getIndex());
                fieldDataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            fieldDataStyleArr[i] = fieldDataStyle;

            CellStyle headStyle = workbook.createCellStyle();
            headStyle.cloneStyleFrom(fieldDataStyle);
            if (headColorIndex > -1) {
                headStyle.setFillForegroundColor((short) headColorIndex);
                headStyle.setFillBackgroundColor((short) headColorIndex);
                headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }

            // head-field data
            Cell cellX = headRow.createCell(i, CellType.STRING);
            cellX.setCellStyle(headStyle);
            cellX.setCellValue(new XSSFRichTextString(fieldName));
        }

        // sheet data rows
        for (int dataIndex = 0; dataIndex < sheetDataList.size(); dataIndex++) {
            int rowIndex = dataIndex + 1;
            Object rowData = sheetDataList.get(dataIndex);

            Row rowX = sheet.createRow(rowIndex);

            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(rowData);
                    boolean isImage = imageArr[i];
                    if (isImage && null != fieldValue) {
                        byte[] bytes = FieldReflectionUtils.formatBytes(field, fieldValue);
                        int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
                        //anchor主要用于设置图片的属性
                        rowX.setHeightInPoints(60);

                        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 1023, 255, (short) i
                                , rowIndex,
                                (short) i + 1,
                                rowIndex + 1);
                        //设置跟随单元格自适应大小，如果设置移动不resetSize，只会在固定位置
                        anchor.setAnchorType(AnchorType.MOVE_DONT_RESIZE);
                        //插入图片
                        patriarch.createPicture(anchor, pictureIdx);
                    } else {
                        String value = FieldReflectionUtils.formatValue(field, fieldValue);
                        Cell cellX = rowX.createCell(i, CellType.STRING);
                        cellX.setCellValue(new XSSFRichTextString(value));
                        cellX.setCellStyle(fieldDataStyleArr[i]);

                        boolean isLink = linkArr[i];

                        //自动添加锚点，需要转换为相对于文件夹内的相对路径
                        if (isLink && !StringUtils.isEmpty(value)) {
                            Hyperlink xssfHyperlink = helper.createHyperlink(HyperlinkType.FILE);
                            xssfHyperlink.setAddress(value);
                            cellX.setHyperlink(xssfHyperlink);
                        }
                    }


                } catch (IllegalAccessException e) {
                    log.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        }
        //自动调整列宽比较耗时，建议不要开启
//    自动调整列宽
//    sheet.trackAllColumnsForAutoSizing();
//    // sheet finally
//    for (int i = 0; i < fields.size(); i++) {
//      int fieldWidth = fieldWidthArr[i];
//      if (fieldWidth > 0) {
//        sheet.setColumnWidth(i, fieldWidth);
//      } else {
//        sheet.autoSizeColumn((short) i);
//      }
//    }
    }

    /**
     * 导出csv 文件
     *
     * @param fileFolder 父文件夹
     * @param fileName   具体文件名
     * @param dataList   数据集合
     */
    public static void exportCsv(String fileFolder, String fileName, List<?> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            log.warn("exportCsv error, data array can not be empty.");
            return;
        }

        File file = new File(fileFolder);
        if (!file.exists()) {
            file.mkdirs();
        }

        String filePath = fileFolder.concat(SPRIT).concat(fileName);

        OutputStream outs = null;
        try {
            outs = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            log.error("exportCsv file not found, ", e);
            return;
        }
        CsvWriter csvWriter = null;
        char delimiter = ',';
        csvWriter = new CsvWriter(outs, delimiter, StandardCharsets.UTF_8);
        try {
            //写入bom头，防止中文乱码
            outs.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
        } catch (IOException e) {
            log.error("exportCsv write bom data error", e);
        }
        try {
            makeCsv(csvWriter, dataList);
        } catch (IOException e) {
            log.error("exportCsv error,", e);
        } finally {
            try {
                outs.close();
            } catch (IOException e) {
                log.error("exportCsv close out stream error", e);
            }
            csvWriter.close();
        }
    }

    /**
     * 生成csv 数据
     *
     * @param csvWriter     CsvWriter
     * @param sheetDataList 数据集合
     * @throws IOException close throws IOException
     */
    private static void makeCsv(CsvWriter csvWriter, List<?> sheetDataList) throws IOException {
        // data
        if (sheetDataList == null || sheetDataList.size() == 0) {
            throw new RuntimeException("makeCsv error, data can not be empty.");
        }

        // sheet
        Class<?> sheetClass = sheetDataList.get(0).getClass();

        // sheet field
        List<Field> fields = Lists.newArrayList();

        for (Field field : sheetClass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            //忽略有导出注解的字段信息
            if (field.getAnnotation(ExportIgnore.class) != null) {
                continue;
            }
            fields.add(field);
        }

        if (CollectionUtils.isEmpty(fields)) {
            throw new RuntimeException("makeCsv error, data field can not be empty.");
        }

        for (int i = 0; i < fields.size(); i++) {

            // field
            Field field = fields.get(i);
            ExportField excelField = field.getAnnotation(ExportField.class);

            String fieldName = field.getName();
            if (excelField != null) {
                if (!StringUtils.isEmpty(excelField.name()) && excelField.name().trim().length() > 0) {
                    fieldName = excelField.name().trim();
                }
            }

            csvWriter.write(fieldName);
        }

        csvWriter.endRecord();
        for (int dataIndex = 0; dataIndex < sheetDataList.size(); dataIndex++) {

            Object rowData = sheetDataList.get(dataIndex);
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                try {
                    field.setAccessible(true);
                    Object value = field.get(rowData);

                    if (value instanceof byte[]) {
                        csvWriter.write("");
                    } else {
                        String valStr = FieldReflectionUtils.formatValue(field, value);

                        csvWriter.write(valStr);
                    }


                } catch (IllegalAccessException e) {
                    log.error(e.getMessage(), e);
                }
            }
            csvWriter.endRecord();
        }

        csvWriter.flush();

    }

    public static <T> void exportCsvTemplate(String fileFolder, String fileName, T data) {

        File file = new File(fileFolder);
        if (!file.exists()) {
            file.mkdirs();
        }

        String filePath = fileFolder.concat(SPRIT).concat(fileName);

        OutputStream outs = null;
        try {
            outs = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            log.error("exportCsv file not found, ", e);
            return;
        }
        CsvWriter csvWriter = null;
        char delimiter = ',';
        csvWriter = new CsvWriter(outs, delimiter, StandardCharsets.UTF_8);
        try {
            //写入bom头，防止中文乱码
            outs.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
        } catch (IOException e) {
            log.error("exportCsv write bom data error", e);
        }
        try {
            makeCsv(csvWriter, data);
        } catch (IOException e) {
            log.error("exportCsv error,", e);
        } finally {
            try {
                outs.close();
            } catch (IOException e) {
                log.error("exportCsv close out stream error", e);
            }
            csvWriter.close();
        }
    }


    private static <T> void makeCsv(CsvWriter csvWriter, T data) throws IOException {
        // sheet
        Class<?> sheetClass = data.getClass();

        // sheet field
        List<Field> fields = Lists.newArrayList();

        for (Field field : sheetClass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            //忽略有导出注解的字段信息
            if (field.getAnnotation(ExportIgnore.class) != null) {
                continue;
            }
            fields.add(field);
        }

        if (CollectionUtils.isEmpty(fields)) {
            throw new RuntimeException("makeCsv error, data field can not be empty.");
        }

        for (Field field : fields) {
            ExportField excelField = field.getAnnotation(ExportField.class);
            String fieldName = field.getName();
            if (excelField != null) {
                if (!StringUtils.isEmpty(excelField.name()) && excelField.name().trim().length() > 0) {
                    fieldName = excelField.name().trim();
                }
            }
            csvWriter.write(fieldName);
        }

        csvWriter.endRecord();

        csvWriter.flush();
    }

}
