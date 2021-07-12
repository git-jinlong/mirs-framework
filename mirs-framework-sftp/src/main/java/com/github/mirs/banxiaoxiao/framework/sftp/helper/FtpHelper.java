package com.github.mirs.banxiaoxiao.framework.sftp.helper;

/**
 * @author: bc
 * @date: 2021-05-27 18:56
 **/
public final class FtpHelper {

    private FtpHelper() {

    }

    private static class FtpHelperHolder {
        private static FtpHelper INSTANCE = new FtpHelper();
    }

    public static FtpHelper getInstance() {
        return FtpHelperHolder.INSTANCE;
    }

    private String FILE_SERVER = "";

    public void setFileServer(String fileServer) {
        FILE_SERVER = fileServer;
    }

    public String loadFileServerPrefix() {
        return FILE_SERVER;
    }
}
