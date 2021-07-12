package com.github.mirs.banxiaoxiao.framework.sftp.service.impl;

import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.sftp.config.FtpClientConfig;
import com.github.mirs.banxiaoxiao.framework.sftp.pool.FTPClientPool;
import com.github.mirs.banxiaoxiao.framework.sftp.service.SftpService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * @author: bc
 * @date: 2021-03-25 12:24
 **/
@Slf4j
public class SftpServiceImpl implements SftpService {

    @Autowired
    private FtpClientConfig ftpClientConfig;


    // 设置第一次登陆的时候提示，可选值：(ask | yes | no)
    private static final String SESSION_CONFIG_STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
    private static Map<String, FTPClientPool> ftpClientPoolMap = Maps.newConcurrentMap();
    //默认key
    private static final String DEF_KEY = "DEF_KEY";

    @Autowired
    public SftpServiceImpl(FtpClientConfig ftpClientConfig) {

        if (StringUtils.isNotBlank(ftpClientConfig.getHost()) && StringUtils.isNotBlank(ftpClientConfig.getUsername()) && StringUtils.isNotBlank(ftpClientConfig.getPassword()) && Objects.nonNull(ftpClientConfig.getPort())) {

            ftpClientPoolMap.put(DEF_KEY, new FTPClientPool(ftpClientConfig));

        } else {
            TComLogs.info("初始化ftpclientPool失败,原因：未配置相关ftp配置.");
        }
    }

    /**
     * 改变目录路径
     *
     * @param directory
     * @param ftp
     * @return
     * @date 创建时间：2017年6月22日 上午11:52:13
     */
    public boolean changeWorkingDirectory(String directory, FTPClient ftp) {
        boolean flag = true;
        try {
            flag = ftp.changeWorkingDirectory(directory);
            if (flag) {
                log.info("进入文件夹" + directory + " 成功！");
            } else {
                log.info("进入文件夹" + directory + " 失败！");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return flag;
    }

    /**
     * 创建目录
     *
     * @param dir
     * @param ftp
     * @return
     * @date 创建时间：2017年6月22日 上午11:52:40
     */
    public boolean makeDirectory(String dir, FTPClient ftp) {
        boolean flag = true;
        try {
            flag = ftp.makeDirectory(dir);
            if (flag) {
                log.info("创建文件夹" + dir + " 成功！");
            } else {
                log.info("创建文件夹" + dir + " 失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 判断ftp服务器文件是否存在
     *
     * @param path
     * @param ftp
     * @return
     * @throws IOException
     * @date 创建时间：2017年6月22日 上午11:52:52
     */
    public static boolean existFile(String path, FTPClient ftp) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftp.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }


    @Override
    public boolean uploadFile(String folder, String fileName, InputStream inputStream) {
        boolean result = false;
        FTPClient ftpClient = null;
        try {
            ftpClient = ftpClientPoolMap.get(DEF_KEY).borrowObject();
        } catch (Exception e) {
            TComLogs.error("争抢FTP失败,{}", e);
            return result;
        }
        FileInputStream input = null;
        try {
            if (Objects.isNull(ftpClient)) {
                TComLogs.info("uploadFile get ftpClient is null");
                return result;
            }
//            if (StringUtils.isEmpty(folder)) {
//                folder = "/";
//            }

//            ftpClient.changeWorkingDirectory(ftpClientConfig.getRootPath());
            String directory = folder + "/";

            String[] split = directory.split("/");

            /*该部分为逐级创建*/
            for (String str : split) {
                if (StringUtils.isEmpty(str)) {
                    continue;
                }
                if (!ftpClient.changeWorkingDirectory(str)) {
                    boolean makeDirectory = ftpClient.makeDirectory(str);
                    boolean changeWorkingDirectory = ftpClient.changeWorkingDirectory(str);
                    log.info(str + "创建：" + makeDirectory + ";切换：" + changeWorkingDirectory);
                }
            }

//            ftpClient.changeWorkingDirectory(ftpClientConfig.getRootPath() + folder);
            if (!ftpClient.storeFile(new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1), inputStream)) {
                TComLogs.info("upload file error,errorCode={}", ftpClient.getReplyString());
                return result;
            }


            inputStream.close();
            result = true;
        } catch (IOException ex) {
            TComLogs.error("ftp 异常：{}", ex);
        } finally {
            //重置工作路径
            boolean reset = false;
            try {
                reset = ftpClient.changeWorkingDirectory("/");
            } catch (IOException e) {
                e.printStackTrace();
            }
            TComLogs.info("reset path ={}", reset);
            try {
                TComLogs.info(ftpClient.printWorkingDirectory());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ftpClientPoolMap.get(DEF_KEY).returnObject(ftpClient);
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public boolean uploadFile(String folder, String fileName, File file) {
        try {
            return this.uploadFile(folder, fileName, new FileInputStream(file));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String downloadFile(String targetPath, String fileName, String localPath) throws Exception {
        String localFilePath = null;
        FTPClient ftp = null;
        try {
            ftp = ftpClientPoolMap.get(DEF_KEY).borrowObject();
        } catch (Exception e) {
            TComLogs.error("争抢FTP失败,{}", e);
        }
        try {
            ftp.changeWorkingDirectory(targetPath);// 转移到FTP服务器目录
            //被动模式（可以提高针对不同FTP服务器的兼容性）
            if (ftpClientConfig.isPassiveMode()) {
                ftp.enterLocalPassiveMode();
            }
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    localFilePath = localPath + "/" + ff.getName();
                    File localFile = new File(localFilePath);
                    OutputStream is = new FileOutputStream(localFile);
                    // ftp需使用ISO-8859-1编码格式
                    String realName = new String(ff.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
                    ftp.retrieveFile(realName, is);
                    is.close();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ftpClientPoolMap.get(DEF_KEY).returnObject(ftp);
        }
        return localFilePath;
    }

    @Override
    public boolean deleteFile(String targetPath) throws Exception {
        FTPClient ftpClient = null;
        try {
            ftpClient = ftpClientPoolMap.get(DEF_KEY).borrowObject();
        } catch (Exception e) {
            TComLogs.error("争抢FTP失败,{}", e);
        }
        if (null == ftpClient) {
            TComLogs.error("deleteFtpFile targetPath={} ftpClient is null", targetPath);
            return false;
        }
        return ftpClient.deleteFile(targetPath);
    }
}
