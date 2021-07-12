package com.github.mirs.banxiaoxiao.framework.common.util;

import org.apache.commons.lang.StringUtils;

/**
 * @author: bc
 * @date: 2021-03-31 14:04
 **/
public class EncryptUtils {


    /**
     * 针对姓名脱敏
     * <pre>
     *   1.如果姓名为空，则直接返回空
     *   2.如果姓名长度为1，直接返回
     *   3.如果姓名长度等于2，则加密第二个字符串
     *   4.如果长度大于2，则隐藏中间内容
     * </pre>
     *
     * @param originName 原始姓名
     */
    public static String encryptName(String originName) {
        if (StringUtils.isEmpty(originName)) {
            return StringUtils.EMPTY;
        }
        String result = "";
        char[] chars = originName.toCharArray();
        if (chars.length == 1) {
            return originName;
        }
        if (chars.length == 2) {
            result = originName.replaceFirst(originName.substring(1), "*");
            return result;
        }
        if (chars.length > 2) {
            result = originName.replaceAll(originName.substring(1, chars.length - 1), "*");
        }
        return result;
    }


}
