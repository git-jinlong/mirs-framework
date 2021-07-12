package com.github.mirs.banxiaoxiao.framework.core.woodpecker.config;

public class CommandPathHolder {

    private static String cmdDir;

    public static String getCmdDir() {
        return cmdDir;
    }

    public static void setCmdDir(String cmdDir) {
        CommandPathHolder.cmdDir = cmdDir;
    }
}
