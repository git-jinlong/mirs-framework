package com.github.mirs.banxiaoxiao.framework.sftp.service;

import java.io.File;
import java.io.InputStream;

/**
 * @author: bc
 * @date: 2021-03-25 12:23
 **/
public interface SftpService {


    boolean uploadFile(String folder, String fileName, InputStream inputStream);

    boolean uploadFile(String folder, String fileName, File file);

    String downloadFile(String targetPath, String fileName, String localPath) throws Exception;

    boolean deleteFile(String targetPath) throws Exception;
}
