package com.github.mirs.banxiaoxiao.framework.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 给图片添加文字水印
 *
 * @author bc
 */
@Slf4j
public class ImageWaterMarkUtil {

    // 水印透明度
    private static float alpha = 0.4f;
    // 水印文字大小
    public static final int FONT_SIZE = 70;
    // 水印文字字体
    private static Font font = new Font("仿宋体", Font.BOLD, FONT_SIZE);
    // 水印文字颜色
    private static Color color = Color.lightGray;
    // 水印之间的间隔
    private static final int XMOVE = 20;
    // 水印之间的间隔
    private static final int YMOVE = 20;


    /**
     * 获取文本长度。汉字为1:1，英文和数字为2:1
     *
     * @param text 水印文字
     * @return 文字的长度
     */
    private static int getTextLength(String text) {
        int length = text.length();
        for (int i = 0; i < text.length(); i++) {
            String s = String.valueOf(text.charAt(i));
            if (s.getBytes().length > 1) {
                length++;
            }
        }
        length = length % 2 == 0 ? length / 2 : length / 2 + 1;
        return length;
    }


    /**
     * 给图片添加水印文字、可设置水印文字的旋转角度
     *
     * @param waterText  待设置的水印文字
     * @param srcImgPath 源文件路径
     * @param targetPath 目标文件路径
     * @param degree     旋转度
     */
    public static void addImageWaterMark(String waterText, String srcImgPath, String targetPath, Integer degree) {

        if (StringUtils.isEmpty(waterText)) {
            waterText = "远程库";
        }

        boolean srcExists = Files.exists(Paths.get(srcImgPath));

        if (!srcExists) {
            log.warn("addImageWaterMark src={} not exists", srcImgPath);
            return;
        }
        long start = System.currentTimeMillis();
        InputStream is = null;
        OutputStream os = null;
        try {
            // 源图片
            Image srcImg = ImageIO.read(new File(srcImgPath));
            int width = srcImg.getWidth(null);// 原图宽度
            int height = srcImg.getHeight(null);// 原图高度
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
            // 得到画笔对象
            Graphics2D g = buffImg.createGraphics();
            // 设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);
            // 设置水印旋转
            if (null != degree) {
                g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
            }
            // 设置水印文字颜色
            g.setColor(color);
            // 设置水印文字Font
            g.setFont(font);
            // 设置水印文字透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

            int x = -width / 2;
            int y = -height / 2;
            int markWidth = FONT_SIZE * getTextLength(waterText);// 字体长度
            int markHeight = FONT_SIZE;// 字体高度

            // 循环添加水印
            while (x < width * 1.5) {
                y = -height / 2;
                while (y < height * 1.5) {
                    g.drawString(waterText, x, y);
                    y += markHeight + YMOVE;
                }
                x += markWidth + XMOVE;
            }
            // 释放资源
            g.dispose();
            // 生成图片
            os = new FileOutputStream(targetPath);
            ImageIO.write(buffImg, "JPG", os);
            long end = System.currentTimeMillis();
            log.debug("addWaterMark cost time={}", end - start);
        } catch (Exception e) {
            log.error("addWaterMark error", e);
        } finally {

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    //ignore ex
                    log.error("addWaterMark close is error", e);
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("addWaterMark close out error", e);
                }
            }

        }

    }

    public static void main(String[] args) {

        String srcImgPath = "e:\\下载.jpg";
        // 水印文字
        String logoText = "15925638968";
        String targerTextPath2 = "e:\\bc_shuiyin.jpg";
        System.out.println("给图片添加水印文字开始...");
        // 给图片添加斜水印文字
        ImageWaterMarkUtil.addImageWaterMark(logoText, srcImgPath, targerTextPath2, -40);
        System.out.println("给图片添加水印文字结束...");


    }

}
