package com.github.mirs.banxiaoxiao.framework.core.log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 通用日志器
 */
public class TComLogs {
  // 方法调用表
  private static Map<String,InvokeInfo> invokeMap = new HashMap<>();
    /**
     * 获取适配日志器，供内部调用
     * 
     * @return
     */
    @SuppressWarnings({ "restriction", "deprecation" })
    private static TComLogger getLocationAwareLogger(final int depth) {
        String className = sun.reflect.Reflection.getCallerClass(depth).getName();
        return FlowLoggers.getLogger(className).tcomLogger();
    }

    private static TComLogger getLogger() {
        return getLocationAwareLogger(4);
    }
    
    public static void debug(String formatMsg, Object... propertys) {
        getLogger().debug(formatMsg, propertys);
    }
    
    public static boolean isDebug() {
        return getLogger().isDebug();
    }

    public static void info(String formatMsg, Object... propertys) {
        getLogger().info(formatMsg, propertys);
    }

  /**
   *
   * 根据方法调用次数控制日志输出频率
   * @param timeWatch 主要用来调试程序，观察程序运行每一步所用的时间
   * @param times 间隔多少次方法调用输出日志
   * @param formatMsg 日志格式
   * @param propertys 占位符参数
   */
    public static void infoCycle(TimeWatch timeWatch,int times, String formatMsg, Object... propertys){
      // 该方法内部加了锁，是否会影响性能？
      StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
      if(stackTraceElements == null || stackTraceElements.length == 0){
        TComLogs.error("TComlogs infoCycle get stack error.");
        return;
      }
      StackTraceElement stackTraceElement = stackTraceElements[2];
      String invoker = stackTraceElement.getClassName() + "-" + stackTraceElement.getMethodName() +
          "-" + stackTraceElement.getLineNumber();
      if(invokeMap.containsKey(invoker)){
        InvokeInfo invokeInfo = invokeMap.get(invoker);
        invokeInfo.increaseTimes();
        if(invokeInfo.getTimes() % times == 0){
          TComLogs.info(timeWatch,formatMsg,propertys);
          invokeInfo.setTimes(0);
          invokeMap.put(invoker,invokeInfo);
          return;
        }
      } else {
        TComLogs.info(formatMsg,propertys);
        InvokeInfo invokeInfo = new InvokeInfo(0,1);
        invokeMap.put(invoker,invokeInfo);
      }
    }

  /**
   *
   * 根据方法调用次数控制日志输出频率
   * @param times 间隔多少次方法调用输出日志
   * @param formatMsg 日志格式
   * @param propertys 占位符参数
   */
    public static void infoCycle(int times, String formatMsg, Object... propertys){
      // 该方法内部加了锁，是否会影响性能？
      StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
      if(stackTraceElements == null || stackTraceElements.length == 0){
        TComLogs.error("TComlogs infoCycle get stack error.");
        return;
      }
      StackTraceElement stackTraceElement = stackTraceElements[2];
      String invoker = stackTraceElement.getClassName() + "-" + stackTraceElement.getMethodName() +
          "-" + stackTraceElement.getLineNumber();
      if(invokeMap.containsKey(invoker)){
        InvokeInfo invokeInfo = invokeMap.get(invoker);
        invokeInfo.increaseTimes();
        if(invokeInfo.getTimes() % times == 0){
          TComLogs.info(formatMsg,propertys);
          invokeInfo.setTimes(0);
          invokeMap.put(invoker,invokeInfo);
          return;
        }
      } else {
        TComLogs.info(formatMsg,propertys);
        InvokeInfo invokeInfo = new InvokeInfo(0,1);
        invokeMap.put(invoker,invokeInfo);
      }
    }

  /**
   * 间隔最少多长时间输出日志
   * @param milliseconds 间隔最少时间，日志输出可能晚于该时间，取决于方法调用频率
   * @param formatMsg 日志格式
   * @param propertys 占位符参数
   */
    public static void infoCycle(long milliseconds, String formatMsg, Object... propertys){
      // 该方法内部加了锁，是否会影响性能？
      StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
      if(stackTraceElements == null || stackTraceElements.length == 0){
        TComLogs.error("TComlogs infoCycle get stack error.");
        return;
      }
      StackTraceElement stackTraceElement = stackTraceElements[2];
      String invoker = stackTraceElement.getClassName() + "-" + stackTraceElement.getMethodName() +
          "-" + stackTraceElement.getLineNumber();
      if(invokeMap.containsKey(invoker)){
        InvokeInfo invokeInfo = invokeMap.get(invoker);
        long now = System.currentTimeMillis();
        if((now - invokeInfo.getLastInvokeTime()) >= milliseconds){
          TComLogs.info(formatMsg,propertys);
          invokeInfo.setLastInvokeTime(now);
          invokeMap.put(invoker,invokeInfo);
          return;
        }
      } else {
        TComLogs.info(formatMsg,propertys);
        InvokeInfo invokeInfo = new InvokeInfo(System.currentTimeMillis(),0);
        invokeMap.put(invoker,invokeInfo);
      }
    }

  /**
   * 间隔最少多少时间输出日志
   * @param timeWatch 主要用来调试程序，观察程序运行每一步所用的时间
   * @param milliseconds 间隔最少时间，日志输出可能晚于该时间，取决于方法调用频率
   * @param formatMsg 日志格式
   * @param propertys 占位符参数
   */
    public static void infoCycle(TimeWatch timeWatch, long milliseconds, String formatMsg, Object... propertys){
      // 该方法内部加了锁，是否会影响性能？
      StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
      if(stackTraceElements == null || stackTraceElements.length == 0){
        TComLogs.error("TComlogs infoCycle get stack error.");
        return;
      }
      StackTraceElement stackTraceElement = stackTraceElements[2];
      String invoker = stackTraceElement.getClassName() + "-" + stackTraceElement.getMethodName() +
          stackTraceElement.getLineNumber();
      if(invokeMap.containsKey(invoker)){
        InvokeInfo invokeInfo = invokeMap.get(invoker);
        long now = System.currentTimeMillis();
        if((now - invokeInfo.getLastInvokeTime()) >= milliseconds){
          TComLogs.info(timeWatch,formatMsg,propertys);
          invokeInfo.setLastInvokeTime(now);
          invokeMap.put(invoker,invokeInfo);
          return;
        }
      } else {
        InvokeInfo invokeInfo = new InvokeInfo(System.currentTimeMillis(),0);
        invokeMap.put(invoker,invokeInfo);
      }
    }

    public static boolean isInfo() {
        return getLogger().isInfo();
    }

    public static void warn(String formatMsg, Object... propertys) {
        getLogger().warn(formatMsg, propertys);
    }

    public static void warn(String formatMsg, Throwable e, Object... propertys) {
        getLogger().warn(formatMsg, e, propertys);
    }

    public static void error(String formatMsg, Throwable e, Object... propertys) {
        getLogger().error(formatMsg, e, propertys);
    }

    public static void error(String formatMsg, Object... propertys) {
        getLogger().error(formatMsg, propertys);
    }

    public static void debug(TimeWatch timeWatch, String formatMsg, Object... propertys) {
        getLogger().debug(timeWatch, formatMsg, propertys);
    }

    public static void info(TimeWatch timeWatch, String formatMsg, Object... propertys) {
        getLogger().info(timeWatch, formatMsg, propertys);
    }

    private static final class DaemonThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            AtomicInteger atomicInteger = new AtomicInteger(0);
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("cycle-log-thread" + atomicInteger.getAndIncrement());
            return thread;
        }
    }
}
