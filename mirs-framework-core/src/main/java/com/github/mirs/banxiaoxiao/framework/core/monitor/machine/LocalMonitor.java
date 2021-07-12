package com.github.mirs.banxiaoxiao.framework.core.monitor.machine;

import com.github.mirs.banxiaoxiao.framework.core.monitor.MonitorExeception;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

public class LocalMonitor {

    private static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    //Heap最大空间
    public static long getHeapMaxSpace(){
        long max = memoryMXBean.getHeapMemoryUsage().getMax();
        if(max <= 0L){
            throw new MonitorExeception("max heap space is <= 0L, error.");
        }
        return max;
    }

    //Heap已使用空间
    public static long getHeapUsedSapce(){
        long used = memoryMXBean.getHeapMemoryUsage().getUsed();
        if(used <= 0L){
            throw new MonitorExeception("used heap space is <= 0L, error.");
        }
        return used;
    }

    //Heap剩余空间
    public static long getHeapUnusedSpace(){
        long max = getHeapMaxSpace();
        long used = getHeapUsedSapce();
        if(max < used){
            throw new MonitorExeception("max heap space is < used space, error.");
        }
        return max - used;
    }

    private static final long KB = 1024;

    private static final long MB = 1024 * KB;

    private static final long GB = 1024 * MB;

    public static long toKB(long byteSize){
        return byteSize / KB;
    }

    public static long toMB(long byteSize){
        return byteSize / MB;
    }

    public static long toGB(long byteSize){
        return byteSize / GB;
    }
}
