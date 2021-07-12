package com.github.mirs.banxiaoxiao.framework.common.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;
import java.util.Base64;
import java.util.Base64.Decoder;

/**
 * @author bc
 * @Date 2020-06-11 16:37
 * @title 请详细描述该类含义
 */
public class AES128 {

    public static boolean initialized = false;

    public static void initialize() {
        if (initialized) {
            return;
        }
        Security.addProvider(new BouncyCastleProvider());
        initialized = true;

    }

    public static String encrypt(byte[] dataByte, byte[] keyByte, byte[] ivByte) throws Exception {
        if (!initialized) {
            initialize();
        }
        String encryptedData = null;

        //指定算法，模式，填充方式，创建一个Cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");

        //生成Key对象
        Key sKeySpec = new SecretKeySpec(keyByte, "AES");

        //把向量初始化到算法参数
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(ivByte));

        //指定用途，密钥，参数 初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, params);

        //指定加密
        byte[] result = cipher.doFinal(dataByte);

        //对结果进行Base64编码，否则会得到一串乱码，不便于后续操作
        Base64.Encoder encoder = Base64.getEncoder();
        encryptedData = encoder.encodeToString(result);

        return encryptedData;
    }

    public static String decrypt(String encryptedData, String sessionKey, String iv) throws Exception {
        if (!initialized) {
            initialize();
        }
        //解密之前先把Base64格式的数据转成原始格式
        Decoder decoder = Base64.getDecoder();
        byte[] dataByte = decoder.decode(encryptedData);
        byte[] keyByte = decoder.decode(sessionKey);
        byte[] ivByte = decoder.decode(iv);

        String data = null;

        //指定算法，模式，填充方法 创建一个Cipher实例
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");

        //生成Key对象
        Key sKeySpec = new SecretKeySpec(keyByte, "AES");

        //把向量初始化到算法参数
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(ivByte));

        //指定用途，密钥，参数 初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec, params);

        //执行解密
        byte[] result = cipher.doFinal(dataByte);

        //解密后转成字符串
        data = new String(result);

        return data;
    }

//  public static void main(String[] args) throws Exception {
//
//    String charset = "utf-8";
//    String data = "banchun";
//
//    String sessionKey = "7T#lVy/EF3#f?Z3g4hy$3WQ2MUP-XBL5";
//    String iv = sessionKey.substring(0, 16);
//    String result = encrypt(data.getBytes(Charset.forName(charset)), sessionKey.getBytes(Charset.forName(charset)),
//        iv.getBytes(Charset.forName(charset)));
//    System.out.println("KLq6pOfwE8bke3zqIYqyCcj+S+TuPLH2a5sCwYI91rI=");
//    String decode = decrypt("KLq6pOfwE8bke3zqIYqyCcj+S+TuPLH2a5sCwYI91rI=", Base64.getEncoder().encodeToString(sessionKey.getBytes()), Base64.getEncoder().encodeToString(iv.getBytes()));
//
//    System.out.println(decode);
//
//  }
}


