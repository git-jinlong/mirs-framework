package com.github.mirs.banxiaoxiao.framework.common.util;

public class DigitUtil {

    private final static char[] DIGITS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_".toCharArray();

    private final static long[] MASKS = {0, 1, 3, 7, 15, 31, 63};

    /**
     * 前面用0补齐
     *
     * @param i
     * @return
     */
    public static String longTo64Str(long i) {
        char[] buf = new char[11]; // 限定11位长度
        int length = 11;
        long mask = 63L;
        do {
            buf[--length] = DIGITS[(int) (i & mask)];
            i >>>= 6;
        } while (i != 0);

        if (length != 0) {
            for (int j = 0; j < length; j++) {
                buf[j] = '0';
            }
        }

        return new String(buf);
    }

    /**
     * @param longs
     * @param length 字符串长度
     * @return
     */
    public static String longArrayTo64Str(long[] longs, int length) {
        char[] buf = new char[length];
        int arrayPosition = longs.length - 1;
        long currentLong = longs[arrayPosition];
        int currentRemainBitLength = 64;
        long mask = 63L;
        for (int i = length - 1; i > -1; i--) {
            if (currentRemainBitLength < 6) {
                arrayPosition -= 1;
                long tempCurrentLong = longs[arrayPosition];
                int nextShiftLength = 6 - currentRemainBitLength;

                buf[i] = DIGITS[(int) (((tempCurrentLong & MASKS[nextShiftLength]) << currentRemainBitLength) + currentLong)];
                currentLong = (tempCurrentLong >>> nextShiftLength);
                currentRemainBitLength = 64 - nextShiftLength;
            } else {
                buf[i] = DIGITS[(int) (currentLong & mask)];
                currentLong >>>= 6;
                currentRemainBitLength -= 6;
            }
        }
        return new String(buf);

    }

    /**
     * @param longs
     * @param length 字符串长度
     * @return
     */
    public static String longArrayTo32Str(long[] longs, int length) {
        char[] buf = new char[length];
        int arrayPosition = longs.length - 1;
        long currentLong = longs[arrayPosition];
        int currentRemainBitLength = 64;
        long mask = 31L;
        for (int i = length - 1; i > -1; i--) {
            if (currentRemainBitLength < 5) {
                arrayPosition -= 1;
                long tempCurrentLong = longs[arrayPosition];
                int nextShiftLength = 5 - currentRemainBitLength;
                buf[i] = DIGITS[(int) (((tempCurrentLong & MASKS[nextShiftLength]) << currentRemainBitLength) + currentLong)];
                currentLong = (tempCurrentLong >>> nextShiftLength);
                currentRemainBitLength = 64 - nextShiftLength;
            } else {
                buf[i] = DIGITS[(int) (currentLong & mask)];
                currentLong >>>= 5;
                currentRemainBitLength -= 5;
            }
        }
        return new String(buf);

    }

    /**
     * 前面用0补齐
     *
     * @param i
     * @return
     */
    public static String longTo32Str(long i) {
        char[] buf = new char[13]; // 限定13位长度
        int length = 13;
        long mask = 31L;
        do {
            buf[--length] = DIGITS[(int) (i & mask)];
            i >>>= 5;
        } while (i != 0);

        if (length != 0) {
            for (int j = 0; j < length; j++) {
                buf[j] = '0';
            }
        }

        return new String(buf);
    }

    /**
     * 前面用0补齐
     *
     * @param i
     * @return
     */
    public static String longTo16Str(long i) {
        char[] buf = new char[16]; // 限定13位长度
        int length = 16;
        long mask = 15L;
        do {
            buf[--length] = DIGITS[(int) (i & mask)];
            i >>>= 4;
        } while (i != 0);

        if (length != 0) {
            for (int j = 0; j < length; j++) {
                buf[j] = '0';
            }
        }
        return new String(buf);
    }
}