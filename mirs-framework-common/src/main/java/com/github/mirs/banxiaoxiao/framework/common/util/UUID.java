/**
 *
 */
package com.github.mirs.banxiaoxiao.framework.common.util;

/**
 * 生成UUID工具类
 *
 * @author erxiao 2016年8月25日
 */
public class UUID {

    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Numbers.toString(hi | (val & (hi - 1)), Numbers.MAX_RADIX).substring(1);
    }

    /**
     * 基于JDK的UUID类生成随机字符串，然后利用64个可打印字符作为64进制字符，将原16进制转换为64进制从而将UUID位数缩短为19位
     *
     * @return
     */
    public static String random19() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        StringBuilder sb = new StringBuilder();
        sb.append(digits(uuid.getMostSignificantBits() >> 32, 8));
        sb.append(digits(uuid.getMostSignificantBits() >> 16, 4));
        sb.append(digits(uuid.getMostSignificantBits(), 4));
        sb.append(digits(uuid.getLeastSignificantBits() >> 48, 4));
        sb.append(digits(uuid.getLeastSignificantBits(), 12));
        return sb.toString();
    }

    public static String random32() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String args[]) {
        System.out.println(java.util.UUID.randomUUID().toString().replace("-", ""));
        System.out.println(UUID.random19());
    }
}
