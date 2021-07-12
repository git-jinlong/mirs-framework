package com.github.mirs.banxiaoxiao.framework.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author bc
 */
@Slf4j
public class DateFormatter {

  public static final String DATE_PATTERN = "yyyy-MM-dd";
  public static final String TIME_PATTERN = "HH:mm";
  public static final String TIME_PARRTERN = "HH:mm:ss";

  public static final String DATE_PATTERN_EXT = "yyyy_MM_dd";

  public static final String VIDEO_TIME_PATTERN = "HH:mm:ss.SSS";
  public static final String VIDEO_FULL_TIME_PATTERN = "HHmmssSSS";

  public static final String DATE_TIME_FULL_PATTERN = "yyyy-MM-dd HH:mm";


  public static final String YEAR_PATTERN = "yyyy";
  public static final String MONTH_PATTERN = "MM";
  public static final String DAY_PATTERN = "dd";
  public static final String TIME_T_PATTERN = "yyyyMMdd'T'HHmm";
  public static final String HOUR_T_PATTERN = "HH";

  public static final String FULL_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

  public static final String T_PATTERN = "yyyyMMdd'T'HHmmssSSS";

  public static final String YYYYMMDDHHMMSS_PATTERN = "yyyyMMddHHmmss";

  public static final String YYYYMMDD = "yyyyMMdd";


  public static String formatFullDate(Long time) {
    DateTimeFormatter df = DateTimeFormatter.ofPattern(FULL_DATE_TIME_PATTERN);
    try {
      String date = df
          .format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneOffset.of("+8")));
      return date;
    } catch (Exception ex) {
      log.error("formatFullDate error,{}", ex);
      return null;
    }
  }

  public static String formatDateHHMM(Long time) {
    DateTimeFormatter df = DateTimeFormatter.ofPattern(TIME_PATTERN);
    try {
      String date = df
          .format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneOffset.of("+8")));
      return date;
    } catch (Exception ex) {
      log.error("formatFullDate error,{}", ex);
      return null;
    }
  }

  public static long formatDateAndTime(String date, String time) {
    try {
      LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_PATTERN));
      LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern(TIME_PATTERN));
      LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
      long start = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
      return start;
    } catch (Exception ex) {
      log.error("formatDateAndTime error,{}", ex);
      return 0;
    }
  }

  /**
   * return date time as long format.
   *
   * @param dateTime String value
   * @return as long
   * @see DateFormatter#DATE_TIME_FULL_PATTERN
   */
  public static long formatDateTime(String dateTime) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FULL_PATTERN);
    LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);

    return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
  }

  /**
   * 转换时间
   *
   * @param time <pre>20171202T214134558</pre>
   * @return 2017-12-02 21:41:34
   */
  public static String parseDate(String time) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssSSS");
    LocalDateTime tempLocal = LocalDateTime.parse(time, formatter);

    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(tempLocal);
  }

  public static String format(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
    return formatter.format(date);
  }

  /**
   * return char sequence as format "yyyyMMdd'T'HHmmssSSS";
   *
   * @see #T_PATTERN
   */
  public static String formatT() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(T_PATTERN);
    return formatter.format(LocalDateTime.now());
  }

  public static String formatVideoTime(String time) {
    if (StringUtils.isEmpty(time)) {
      return "";
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(VIDEO_FULL_TIME_PATTERN);
    DateTimeFormatter resultformatter = DateTimeFormatter.ofPattern(VIDEO_TIME_PATTERN);

    LocalTime localTime = LocalTime.parse(time, formatter);

    return resultformatter.format(localTime);
  }

  public static int formatVideoTimeToLong(String time) {

    if (StringUtils.isEmpty(time)) {
      return 0;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(VIDEO_FULL_TIME_PATTERN);

    LocalTime localTime = LocalTime.parse(time, formatter);
    return localTime.toSecondOfDay();
  }

  public static String formatHHMM() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_T_PATTERN);
    return formatter.format(LocalDateTime.now());
  }

  public static String formatHH() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(HOUR_T_PATTERN);
    return formatter.format(LocalDateTime.now());
  }

  public static String formateDate(LocalDateTime date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FULL_DATE_TIME_PATTERN);
    return formatter.format(date);
  }

  public static String formateDate(LocalDateTime date, String datePattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
    return formatter.format(date);
  }

  public static String formateDate(String date, String datePattern) {
    LocalDateTime localDateTime = LocalDateTime
        .parse(date, DateTimeFormatter.ofPattern(FULL_DATE_TIME_PATTERN));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
    return formatter.format(localDateTime);
  }

  public static String formatYear() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YEAR_PATTERN);
    return formatter.format(LocalDateTime.now());
  }

  public static String formatMonth() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MONTH_PATTERN);
    return formatter.format(LocalDateTime.now());
  }

  public static String formatDay() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DAY_PATTERN);
    return formatter.format(LocalDateTime.now());
  }

  public static LocalDateTime parseLocalDateTime(String datetime) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FULL_DATE_TIME_PATTERN);

    return LocalDateTime.parse(datetime, formatter);
  }

  public static LocalDateTime parseLocalDateTime(String datetime, String datePattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

    return LocalDateTime.parse(datetime, formatter);
  }

  public static LocalDateTime parseDateTime(String datetime) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS_PATTERN);
    return LocalDateTime.parse(datetime, formatter);
  }

  public static LocalDateTime parseFullDateTime(String datetime) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FULL_DATE_TIME_PATTERN);
    return LocalDateTime.parse(datetime, formatter);
  }

  public static String formatVideoDateTime(Long time) {
    if (null == time) {
      return "";
    }
    if (0L == time) {
      return "0";
    }

    DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm:ss");
    try {
      String date = df
          .format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneOffset.of("+0")));
      return date;
    } catch (Exception ex) {
      log.error("formatFullDate error,{}", ex);
      return null;
    }
  }

  public static String formatVideoDateTimeHHMMSSSSS(Long time) {
    if (null == time) {
      return "";
    }
    if (0L == time) {
      return "0";
    }

    DateTimeFormatter df = DateTimeFormatter.ofPattern("HHmmssSSS");
    try {
      String date = df
          .format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneOffset.of("+0")));
      return date;
    } catch (Exception ex) {
      log.error("formatFullDate error,{}", ex);
      return null;
    }
  }

  public static String formatDate(LocalDate localDate) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN_EXT);

    return formatter.format(localDate);
  }


  /**
   * 计算两个日期天数差
   *
   * @param startDate 开始日期
   * @param endDate 结束日期
   * @return 相差的天数
   */
  public static long calcDaysBetween(LocalDate startDate, LocalDate endDate) {
    long days = ChronoUnit.DAYS.between(startDate, endDate);
    return days;
  }

  public static Date convert(LocalDateTime localDateTime) {
    if (null == localDateTime) {
      return null;
    }
    ZoneId zoneId = ZoneId.systemDefault();
    ZonedDateTime zdt = localDateTime.atZone(zoneId);

    Date date = Date.from(zdt.toInstant());
    return date;
  }

  /**
   * parse date to long,date format yyyy-MM-dd HH:mm:ss
   */
  public static long parseDateToLong(String datetime) {
    if (StringUtils.isEmpty(datetime)) {
      return 0;
    }
    LocalDateTime localDateTime = LocalDateTime
        .parse(datetime, DateTimeFormatter.ofPattern(FULL_DATE_TIME_PATTERN));
    long start = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    return start;
  }

  /**
   * calc birth to age,
   *
   * @param birth eg. 19901112
   * @return age
   */
  public static Long calcBirthToAge(String birth) {

    if (StringUtils.isEmpty(birth)) {
      return null;
    }

    return LocalDate.parse(birth, DateTimeFormatter.ofPattern(YYYYMMDD))
        .until(LocalDate.now(), ChronoUnit.YEARS);
  }

  /**
   * Return date as long after formatting {@link LocalDate} with {@link #YYYYMMDD} and convert to
   * long.
   *
   * @param localDate date
   * @return date as long
   */
  public static Long formatYYYYMMDDToLong(LocalDate localDate) {
    String date = DateTimeFormatter.ofPattern(YYYYMMDD).format(localDate);

    return Long.valueOf(date);
  }

  /**
   * 计算两个日期的小时差，日期请保证在同一天，如果不保证可能会返回错误结果， 如果00:00:00 - 23:59:59 严格意义上不是一天，目前严格比对小时和分钟，这也算是给潜规则吧
   *
   * @param start 开始日期
   * @param end 结束日期
   * @return hours
   */
  public static int calcIntervalHours(LocalDateTime start, LocalDateTime end) {
    if (null == start || null == end) {
      return 0;
    }
    Duration duration = Duration.between(start, end);

    int min = end.getMinute() - start.getMinute();
    int hour = end.getHour() - start.getHour();
    if (hour == 23 && min == 59) {
      return 24;
    }
    Long ret = duration.getSeconds() / 3600;

    return ret.intValue();
  }
}
