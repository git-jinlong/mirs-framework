package com.github.mirs.banxiaoxiao.framework.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @author: bc
 * @date: 2021-03-30 11:17
 **/
@Slf4j
public class OSInfo {

    public static final int WINDOW = 0x10;
    public static final int LINUX = 0x20;
    public static final int CENTOS = 0x21;
    public static final int UBUNTU = 0x22;

    private static int os = 0;

    /**
     * @return window, centos or ubuntu
     */
    public static String getOSName() {
        switch (getOS()) {
            case WINDOW:
                return "window";
            case CENTOS:
                return "centos";
            case UBUNTU:
                return "ubuntu";
            default:
                break;
        }
        return "";
    }

    private static boolean isCentOS() {
        File f = new File("/etc/centos-release");
        return f.exists();
    }

    /**
     * @return {@link #WINDOW}, {@value #CENTOS}, {@link #UBUNTU}, or 0 if unknown.
     */
    public static int getOS() {
        if (os == 0) {
            String osname = System.getProperty("os.name");
            if (osname == null) {
                osname = "";
            }
            osname = osname.toLowerCase();
            if (osname.indexOf("win") != -1) {
                os = WINDOW;
            } else if (osname.indexOf("linux") != -1) {
                os = isCentOS() ? CENTOS : UBUNTU;
            }
            log.info("getOS: osname=" + osname + " os=" + os);
        }
        return os;
    }
}
