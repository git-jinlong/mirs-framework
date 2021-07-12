package com.github.mirs.banxiaoxiao.framework.common.util;

import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * @author bc
 */
public final class NumberFormatter {

  public static String formatDouble(double d) {
    NumberFormat nf = NumberFormat.getNumberInstance();

    // 保留两位小数
    nf.setMaximumFractionDigits(2);

    // 如果不需要四舍五入，可以使用RoundingMode.DOWN
    nf.setRoundingMode(RoundingMode.UP);

    return nf.format(d);
  }


  public static int formatVideoHight(double width, double y, double x) {
    double r = width * y / x;
    Double ret = Math.ceil(r);
    return ret.intValue();
  }


}
