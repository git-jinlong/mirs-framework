package com.github.mirs.banxiaoxiao.framework.core.log;

/**
 *
 * 调用信息
 *
 * @author zrh
 *
 **/
public class InvokeInfo {
  // 上次调用时间
  private long lastInvokeTime;
  // 调用次数
  private int times;

  public InvokeInfo(long lastInvokeTime, int times) {
    this.lastInvokeTime = lastInvokeTime;
    this.times = times;
  }

  public long getLastInvokeTime() {
    return lastInvokeTime;
  }

  public void setLastInvokeTime(long lastInvokeTime) {
    this.lastInvokeTime = lastInvokeTime;
  }

  public int getTimes() {
    return times;
  }

  public void setTimes(int times) {
    this.times = times;
  }

  public void increaseTimes(){
    this.times++;
  }
}
