package com.github.mirs.banxiaoxiao.framework.common.util;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author zcy 2019年8月29日
 */
@Slf4j
public class FileUtils {

//    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(1, 30, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
//            new CommandThreadFactory());
//
//    private static class CommandThreadFactory implements ThreadFactory {
//
//        private final AtomicInteger count = new AtomicInteger(1);
//
//        private final ThreadGroup group = new ThreadGroup("shell-exec-thread-group");
//
//        @Override
//        public Thread newThread(Runnable r) {
//            Thread thread = new Thread(this.group, r, "shell-collect-thread-" + count.getAndIncrement());
//            thread.setDaemon(true);
//            thread.setPriority(NORM_PRIORITY);
//            return thread;
//        }
//    }

    /**
     * 用于遍历大目录
     * 
     * @param baseDir
     * @param timeout
     * @param consumer
     * @return
     */
    public static List<File> listFileForLinux(String baseDir, long timeout, final Consumer<File> consumer) {
        List<File> result = new ArrayList<File>();
        String cmd = "ls -l -f " + baseDir;
        Process process = null;
        try {
            ProcessBuilder pb = new ProcessBuilder(new String[] { "/bin/sh", "-c", cmd });
            pb.redirectErrorStream(true);
            process = pb.start();
            InputStream input = process.getInputStream();
            Scanner scanner = new Scanner(input);
            try {
                while (scanner.hasNextLine()) {
                    try {
                        String line = scanner.nextLine();
                        if (line.equals(".") || line.equals("..")) {
                            continue;
                        }
                        File file = new File(baseDir + File.separator + line);
                        if (!file.exists()) {
                            continue;
                        }
                        result.add(file);
                        if (consumer != null) {
                            consumer.accept(file);
                        }
                        if (file.isDirectory()) {
                            listFileForLinux(file.getPath(), timeout, file1 -> {
                                consumer.accept(file1);
                                result.add(file1);
                            });
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                scanner.close();
            }
            CommandUtils.waitFor(process, timeout);
            IOUtils.closeQuietly(input);
        } catch (InterruptedException | IOException | TimeoutException e) {
            return null;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    /**
     * 创建目录
     * 
     * @param dir
     *            目录
     */
    public static void mkdir(String dir) {
        try {
            String dirTemp = dir;
            File dirPath = new File(dirTemp);
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }
        } catch (Exception e) {
            log.error("创建目录操作出错: " + e.getMessage(), e);
        }
    }

    /**
     * 新建文件
     * 
     * @param fileName
     *            String 包含路径的文件名 如:E:\phsftp\src\123.txt
     * @param content
     *            String 文件内容
     * 
     */
    public static void createNewFile(String fileName, String content) {
        try {
            String fileNameTemp = fileName;
            File filePath = new File(fileNameTemp);
            if (!filePath.exists()) {
                filePath.createNewFile();
            }
            FileWriter fw = new FileWriter(filePath);
            PrintWriter pw = new PrintWriter(fw);
            String strContent = content;
            pw.println(strContent);
            pw.flush();
            pw.close();
            fw.close();
        } catch (Exception e) {
            log.error("新建文件操作出错: " + e.getMessage(), e);
        }
    }

    /**
     * 删除文件
     * 
     * @param fileName
     *            包含路径的文件名
     */
    public static void delFile(String fileName) {
        try {
            String filePath = fileName;
            File delFile = new File(filePath);
            delFile.delete();
        } catch (Exception e) {
            log.error("删除文件操作出错: " + e.getMessage(), e);
        }
    }

    /**
     * 删除文件夹
     *
     * @param folderPath
     *            文件夹路径
     */
    public static void delFolder(String folderPath) {
        try {
            // 删除文件夹里面所有内容
            delAllFile(folderPath);
            String filePath = folderPath;
            File myFilePath = new File(filePath);
            // 删除空文件夹
            myFilePath.delete();
        } catch (Exception e) {
            log.error("删除文件夹操作出错" + e.getMessage(), e);
        }
    }

    /**
     * 删除文件夹里面的所有文件
     * 
     * @param path
     *            文件夹路径
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] childFiles = file.list();
        File temp = null;
        if (childFiles != null) {
            for (int i = 0; i < childFiles.length; i++) {
                // File.separator与系统有关的默认名称分隔符
                // 在UNIX系统上，此字段的值为'/'；在Microsoft Windows系统上，它为 '\'。
                if (path.endsWith(File.separator)) {
                    temp = new File(path + childFiles[i]);
                } else {
                    temp = new File(path + File.separator + childFiles[i]);
                }
                if (temp.isFile()) {
                    temp.delete();
                }
                if (temp.isDirectory()) {
                    delAllFile(path + "/" + childFiles[i]);// 先删除文件夹里面的文件
                    delFolder(path + "/" + childFiles[i]);// 再删除空文件夹
                }
            }
        }
    }

    /**
     * 复制单个文件
     * 
     * @param srcFile
     *            包含路径的源文件 如：E:/phsftp/src/abc.txt
     * @param dirDest
     *            目标文件目录；若文件目录不存在则自动创建 如：E:/phsftp/dest
     * @throws IOException
     */
    public static void copyFile(String srcFile, String dirDest) {
        try {
            FileInputStream in = new FileInputStream(srcFile);
            mkdir(dirDest);
            FileOutputStream out = new FileOutputStream(dirDest + "/" + new File(srcFile).getName());
            int len;
            byte buffer[] = new byte[1024];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            out.close();
            in.close();
        } catch (Exception e) {
            log.error("复制文件操作出错:" + e.getMessage(), e);
        }
    }

    /**
     * 复制文件夹
     * 
     * @param oldPath
     *            String 源文件夹路径 如：E:/phsftp/src
     * @param newPath
     *            String 目标文件夹路径 如：E:/phsftp/dest
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {
        try {
            // 如果文件夹不存在 则新建文件夹
            mkdir(newPath);
            File file = new File(oldPath);
            String[] files = file.list();
            File temp = null;
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (oldPath.endsWith(File.separator)) {
                        temp = new File(oldPath + files[i]);
                    } else {
                        temp = new File(oldPath + File.separator + files[i]);
                    }
                    if (temp.isFile()) {
                        FileInputStream input = new FileInputStream(temp);
                        FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                        byte[] buffer = new byte[1024 * 2];
                        int len;
                        while ((len = input.read(buffer)) != -1) {
                            output.write(buffer, 0, len);
                        }
                        output.flush();
                        output.close();
                        input.close();
                    }
                    if (temp.isDirectory()) {// 如果是子文件夹
                        copyFolder(oldPath + "/" + files[i], newPath + "/" + files[i]);
                    }
                }
            }
        } catch (Exception e) {
            log.error("复制文件夹操作出错:" + e.getMessage(), e);
        }
    }

    /**
     * 移动文件到指定目录
     * 
     * @param oldPath
     *            包含路径的文件名 如：E:/phsftp/src/ljq.txt
     * @param newPath
     *            目标文件目录 如：E:/phsftp/dest
     */
    public static void moveFile(String oldPath, String newPath) {
        copyFile(oldPath, newPath);
        delFile(oldPath);
    }

    /**
     * 移动文件到指定目录，不会删除文件夹
     * 
     * @param oldPath
     *            源文件目录 如：E:/phsftp/src
     * @param newPath
     *            目标文件目录 如：E:/phsftp/dest
     */
    public static void moveFiles(String oldPath, String newPath) {
        copyFolder(oldPath, newPath);
        delAllFile(oldPath);
    }

    /**
     * 移动文件到指定目录，会删除文件夹
     * 
     * @param oldPath
     *            源文件目录 如：E:/phsftp/src
     * @param newPath
     *            目标文件目录 如：E:/phsftp/dest
     */
    public static void moveFolder(String oldPath, String newPath) {
        copyFolder(oldPath, newPath);
        delFolder(oldPath);
    }

    /**
     * 解压zip文件
     * 
     * @param srcDir
     *            解压前存放的目录
     * @param destDir
     *            解压后存放的目录
     * @throws Exception
     */
    // public static void jieYaZip(String srcDir, String destDir) throws Exception {
    // int leng = 0;
    // byte[] b = new byte[1024 * 2];
    // /** 获取zip格式的文件 **/
    // File[] zipFiles = new FileFilterByExtension("zip").getFiles(srcDir);
    // if (zipFiles != null && !"".equals(zipFiles)) {
    // for (int i = 0; i < zipFiles.length; i++) {
    // File file = zipFiles[i];
    // /** 解压的输入流 * */
    // ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
    // ZipEntry entry = null;
    // while ((entry = zis.getNextEntry()) != null) {
    // File destFile = null;
    // if (destDir.endsWith(File.separator)) {
    // destFile = new File(destDir + entry.getName());
    // } else {
    // destFile = new File(destDir + "/" + entry.getName());
    // }
    // /** 把解压包中的文件拷贝到目标目录 * */
    // FileOutputStream fos = new FileOutputStream(destFile);
    // while ((leng = zis.read(b)) != -1) {
    // fos.write(b, 0, leng);
    // }
    // fos.close();
    // }
    // zis.close();
    // }
    // }
    // }
    /**
     * 压缩文件
     * 
     * @param srcDir
     *            压缩前存放的目录
     * @param destDir
     *            压缩后存放的目录
     * @throws Exception
     */
    public static void yaSuoZip(String srcDir, String destDir) throws Exception {
        String tempFileName = null;
        byte[] buf = new byte[1024 * 2];
        int len;
        // 获取要压缩的文件
        File[] files = new File(srcDir).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    if (destDir.endsWith(File.separator)) {
                        tempFileName = destDir + file.getName() + ".zip";
                    } else {
                        tempFileName = destDir + "/" + file.getName() + ".zip";
                    }
                    FileOutputStream fos = new FileOutputStream(tempFileName);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    ZipOutputStream zos = new ZipOutputStream(bos);// 压缩包
                    ZipEntry ze = new ZipEntry(file.getName());// 压缩包文件名
                    zos.putNextEntry(ze);// 写入新的ZIP文件条目并将流定位到条目数据的开始处
                    while ((len = bis.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                        zos.flush();
                    }
                    bis.close();
                    zos.close();
                }
            }
        }
    }

    /**
     * 读取数据
     * 
     * @param inSream
     * @param charsetName
     * @return
     * @throws Exception
     */
    public static String readData(InputStream inSream, String charsetName) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inSream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inSream.close();
        return new String(data, charsetName);
    }

    /**
     * 一行一行读取文件，适合字符读取，若读取中文字符时会出现乱码
     * 
     * @param path
     * @return
     * @throws Exception
     */
    public static Set<String> readFile(String path) throws Exception {
        Set<String> datas = new HashSet<String>();
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        while ((line = br.readLine()) != null) {
            datas.add(line);
        }
        br.close();
        fr.close();
        return datas;
    }

    /**
     * 通过文件路径 获取文件名 除掉后缀
     * 
     * @param filePath
     * @return
     */
    public static String getFilename(String filePath) {
        String filename = null;
        {
            int last = filePath.lastIndexOf('\\');
            if (last < 0) {
                last = filePath.lastIndexOf('/');
            }
            if (last >= 0) {
                filename = filePath.substring(last + 1);
            }
        }
        if (filename != null) {
            int last = filename.lastIndexOf('.');
            if (last > 0) {
                filename = filename.substring(0, last);
            }
        }
        return filename;
    }

    /**
     * 通过文件路径 获取文件名 保留后缀
     * 
     * @param filePath
     * @return
     */
    public static String getFilenameWithSuffix(String filePath) {
        String filename = null;
        {
            int last = filePath.lastIndexOf('\\');
            if (last < 0) {
                last = filePath.lastIndexOf('/');
            }
            if (last >= 0) {
                filename = filePath.substring(last + 1);
            }
        }
        return filename;
    }

    /**
     * 通过图片路径获取图片 并转化为byte数组
     * 
     * @param path
     * @return
     */
    public static byte[] imageToByte(String path) {
        byte[] dataRGB24 = null;
        FileImageInputStream input = null;
        try {
            input = new FileImageInputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            dataRGB24 = output.toByteArray();
            output.close();
            input.close();
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return dataRGB24;
    }

    /**
     * byte转图片
     * 
     * @param data
     * @param path
     */
    public static void byteToimage(byte[] data, String path) {
        if (data.length < 3 || ("").equals(path)) {
            return;
        }
        try {
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
            log.info("Make Picture success,Please find image in " + path);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public static void perHourDelete(String path, int timeRange) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                log.info("删除文件不存在" + path);
                return;
            }
            File[] list = file.listFiles();
            if (list == null) {
                log.info("There is no subfolder");
                return;
            }
            for (int i = 0; i < list.length; i++) {
                File[] subfolder = list[i].listFiles();
                for (int j = 0; j < subfolder.length; j++) {
                    long fileTime = subfolder[j].lastModified();
                    long compareTime = System.currentTimeMillis() - (timeRange * 60000);
                    if (fileTime < compareTime) {
                        subfolder[j].delete();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error deleting file");
        }
    }

    @SuppressWarnings("unused")
    private static Long millisecond(int hour) {
        return (long) (hour * (60 * 60 * 1000));
    }

    public static boolean isExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * @param imgStr base64编码字符串
     * @param path   图片路径-具体到文件
     * @return
     * @Description: 将base64编码字符串转换为图片
     * @Author:
     * @CreateTime:
     */
    public static boolean generateImage(String imgStr, String path) {
        if (imgStr == null) {
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // 解密
            byte[] b = decoder.decodeBuffer(imgStr);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(path);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return
     * @Description: 根据图片地址转换为base64编码字符串
     * @Author:
     * @CreateTime:
     */
    public static String getImageStr(String imgFile) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 加密
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    /**
     * 将图片文件转换成base64字符串，参数为该图片的路径
     *
     * @param imageFile
     * @return java.lang.String
     */
    public static String ImageToBase64(String imageFile) {
        InputStream in = null;
        byte[] data = null;

        // 读取图片字节数组
        try {
            in = new FileInputStream(imageFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();

        if (data != null) {
            return "data:image/jpeg;base64," + encoder.encode(data);// 返回Base64编码过的字节数组字符串
        }
        return null;
    }

    /**
     * 将base64解码成图片并保存在传入的路径下
     * 第一个参数为base64 ，第二个参数为路径
     *
     * @param base64, imgFilePath
     * @return boolean
     */
    public static boolean Base64ToImage(String base64, String imgFilePath) {
        // 对字节数组字符串进行Base64解码并生成图片
        if (base64 == null) // 图像数据为空
        {return false;}
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] b = decoder.decodeBuffer(base64);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

}
