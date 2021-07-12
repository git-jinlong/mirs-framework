/**
 *
 */
package com.github.mirs.banxiaoxiao.framework.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author erxiao
 *
 */
public class TimeUtil {
    /**
     * 日期格式 yyyy-MM-dd
     */
    public static final String FORMAT_YYYYMMDD = "yyyy-MM-dd";

    /**
     * 日期格式 yyyy年M月d日
     */
    private static final String FORMAT_YYYYMD = "yyyy年M月d日";

    /**
     * 日期格式 yyyy-MM-dd HH:mm
     */
    public static final String FORMAT_YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";

    /**
     * 日期格式 yyyy-MM-dd HH:mm:ss
     */
    public static final String FORMAT_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 日期格式 yyyyMMddHHmmssSSS
     */
    public static final String FORMAT_YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";

    public static final String FORMAT_LONG_DAY = "yyyyMMdd";

    public static final String FORMAT_LONG_HOUR = "yyyyMMddHH";

    /**
     * 日期格式 yyyy-MM-dd HH:mm:ss:SSS
     */
    public static final String DETAIL_TIME = "yyyy-MM-dd HH:mm:ss:SSS";

    /**
     * 转换为{@link #FORMAT_YYYYMMDDHHMMSS}的字符串格式
     *
     * @param millSec
     * @return
     */
    public static String longToStrDate(long millSec) {
        return longToStrDate(FORMAT_YYYYMMDDHHMMSS, millSec);
    }

    public static String longToStrDate(String format, long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date(millSec);
        return sdf.format(date);
    }

    /**
     * @param format
     * @param strTime
     * @return
     */
    public static long strToLong(String format, String strTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date date = sdf.parse(strTime);
            return date.getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    public static String format(String format, Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(time);
    }

    public static long getCurrentLongDay() {
        SimpleDateFormat formater = new SimpleDateFormat(FORMAT_LONG_DAY);
        return Long.parseLong(formater.format(new Date()));
    }

    public static long getCurrentLongHour() {
        SimpleDateFormat formater = new SimpleDateFormat(FORMAT_LONG_HOUR);
        return Long.parseLong(formater.format(new Date()));
    }

    /**
     * 取得现在的时间 日期格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getNowTime() {
        SimpleDateFormat formater = new SimpleDateFormat(FORMAT_YYYYMMDDHHMMSS);
        return formater.format(new Date());
    }

    /**
     * 取得现在的时间 日期格式 yyyyMMddHHmmssSSS
     */
    public static String getNowTimeS() {
        SimpleDateFormat formater = new SimpleDateFormat(FORMAT_YYYYMMDDHHMMSSSSS);
        return formater.format(new Date());
    }

    public static Long getNowLongTimeS() {
        SimpleDateFormat formater = new SimpleDateFormat(FORMAT_YYYYMMDDHHMMSSSSS);
        return Long.parseLong(formater.format(new Date()));
    }

    /**
     * 日期转换 yyyy年M月d日转成yyyy-MM-dd
     */
    public static String convertYmd(String ymd) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYYMD);
        SimpleDateFormat sdf2 = new SimpleDateFormat(FORMAT_YYYYMMDD);
        try {
            return sdf2.format(sdf.parse(ymd));
        } catch (ParseException e) {
        }
        return null;
    }

    public static Date convertDate(String ymd, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(ymd);
        } catch (ParseException e) {
        }
        return null;
    }

    /**
     * 日期文字转换日期 yyyy年M月d日转成日期
     */
    public static Date convertDate(String ymd) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYYMMDD);
        try {
            return sdf.parse(ymd);
        } catch (ParseException e) {
        }
        return null;
    }

    /**
     * 日期转成日期文字yyyy-MM-dd
     */
    public static String convertDateYmd(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYYMMDD);
        return sdf.format(date);
    }

    /**
     * 日期相加
     *
     * @param date 日期
     * @param year 年
     * @param month 月
     * @param day 日
     * @return 相加后的日期
     */
    public static Date addDate(Date date, int year, int month, int day) {
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.YEAR, year);
        cl.add(Calendar.MONTH, month);
        cl.add(Calendar.DAY_OF_MONTH, day);
        return cl.getTime();
    }

    /**
     * 获取当天最大时间时间
     */
    public static Date getCurrentDate() {
        Calendar cl = Calendar.getInstance();
        cl.setTime(new Date());
        cl.add(Calendar.DAY_OF_MONTH, 1);
        cl.set(Calendar.HOUR_OF_DAY, 0);
        cl.set(Calendar.MINUTE, 0);
        cl.set(Calendar.SECOND, 0);
        cl.set(Calendar.MILLISECOND, 0);
        return cl.getTime();
    }

    /**
     * 获得当天0点时间
     *
     * @return
     */
    public static Date getCurrentBeginDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取指定日期的00:00:00点时间，默认为当天
     *
     * @return
     */
    public static Date getCurrentBeginDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date == null ? new Date() : date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获得当天23:59:59点时间
     *
     * @return
     */
    public static Date getCurrentEndDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, -1);
        return cal.getTime();
    }

    /**
     * 获取指定日期的23:59:59点时间，默认为当天
     *
     * @return
     */
    public static Date getCurrentEndDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date == null ? new Date() : date);
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, -1);
        return cal.getTime();
    }

    /**
     * 计算出两个日期之间相差的天数，7.1 - 7.2，这表示相差2天，包含了开始当天和结束当天
     * date2-date1
     * @param date1 日期1
     * @param date2 日期2
     * @return date1 - date2
     */
    public static int dayInterval(Date d1, Date d2) {
        long time = d2.getTime() - d1.getTime();
        long day = time / 3600000 / 24 + 1;
        return new Long(day).intValue();
    }

    public static void main(String args[]) throws ParseException {
        String time1 = "2015-07-19 18:00:09";
        String time2 = "2015-07-19 17:00:09";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dayInterval(sdf.parse(time1), sdf.parse(time2)));
    }
}
