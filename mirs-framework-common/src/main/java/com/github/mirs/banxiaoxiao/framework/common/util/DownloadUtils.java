package com.github.mirs.banxiaoxiao.framework.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author bc
 */
@Slf4j
public final class DownloadUtils {

    private DownloadUtils() {

    }

    /**
     * 下载远程信息到本地
     *
     * @param remoteURL remote url
     * @param folder    local folder
     * @param fileName  local fileName
     */
    public static void download(String remoteURL, String folder, String fileName) {

        // 构造URL
        URL url = null;
        try {
            url = new URL(remoteURL);
        } catch (MalformedURLException e) {
            log.error("download={} init url error", remoteURL, e);
            return;
        }
        // 打开连接
        URLConnection con = null;
        try {
            con = url.openConnection();
        } catch (IOException e) {
            log.error("download={} open url connection error", remoteURL, e);
            return;
        }
        //设置请求超时为5s
        con.setConnectTimeout(30 * 1000);
        // 输入流
        InputStream is = null;
        try {
            is = con.getInputStream();
        } catch (IOException e) {
            log.error("download={} get input stream error", remoteURL, e);
            return;
        }

        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流

        boolean isExist = Files.exists(Paths.get(folder));
        if (!isExist) {
            try {
                Files.createDirectories(Paths.get(folder));
            } catch (IOException e) {
                log.error("download={} create folder={} error", remoteURL, folder, e);
                return;
            }
        }

        String savePath = folder + File.separator + fileName;
        OutputStream os = null;
        try {
            os = new FileOutputStream(savePath);
        } catch (FileNotFoundException e) {
            log.error("download={} file output  error", remoteURL, e);
            return;
        }
        try {
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完毕，关闭所有链接
        } catch (Exception e) {
            log.error("download={} write file  error", remoteURL, e);
        } finally {
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                //ignore ex
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    //ignore ex
                }
            }
        }
    }

}
