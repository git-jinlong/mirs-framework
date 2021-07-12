package com.github.mirs.banxiaoxiao.framework.core.encryt;

import com.github.mirs.banxiaoxiao.framework.common.util.IOUtils;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.config.Constants;
import org.springframework.core.io.ClassPathResource;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author zcy 2019年9月12日
 */
public class CipherUtil {

    public static final String KEY_ALGORITHM_AES = "AES";

    public static final String KEY_ALGORITHM_DES = "DES";

    private static final String DEFAULT_CIPHER_AES_ECB = "AES/ECB/PKCS5Padding";// 默认的加密算法

    public static byte[] encrypt(byte[] content, String key) {
        return encrypt(content, key, DEFAULT_CIPHER_AES_ECB);
    }

    public static byte[] encrypt(byte[] content, String key, String algorithm) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);// 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key, algorithm));// 初始化为加密模式的密码器
            byte[] result = cipher.doFinal(content);// 加密
            return result;
        } catch (Exception ex) {
            Logger.getLogger(Cipher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static byte[] decrypt(byte[] content, String key) {
        return decrypt(content, key, DEFAULT_CIPHER_AES_ECB);
    }

    public static byte[] decrypt(byte[] content, String key, String algorithm) {
        try {
            // 实例化
            Cipher cipher = Cipher.getInstance(algorithm);
            // 使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key, algorithm));
            byte[] result = cipher.doFinal(content);// 解密
            return result;
        } catch (Exception ex) {
            Logger.getLogger(Cipher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static byte[] encryptc(byte[] content) {
        return Encrypt.encrypt(content);
    }

    public static byte[] decryptc(byte[] content) {
        return Decrypt.decrypt(content);
    }

    public static byte[] encryptcr(byte[] content) {
        byte[] arrayContent = rearray(content);
        byte[] encryptBytes = Encrypt.encrypt(arrayContent);
        return encryptBytes;
    }

    public static byte[] decryptcr(byte[] content) {
        byte[] decryptBytes = Decrypt.decrypt(content);
        byte[] arrayContent = rearray(decryptBytes);
        return arrayContent;
    }


    private static int BLOCK_SIZE = 20;

    private static byte[] rearray(byte[] content) {
        if (content == null) {
            return null;
        }
        int length = content.length;
        int blockNum = length / BLOCK_SIZE;
        if (blockNum < 2) {
            return content;
        }
        int startIndex1, endIndex1, startIndex2;
        if (blockNum == 2) {
            startIndex1 = 0;
            endIndex1 = BLOCK_SIZE;
            startIndex2 = BLOCK_SIZE;
        } else {
            startIndex1 = BLOCK_SIZE;
            endIndex1 = BLOCK_SIZE * 2;
            startIndex2 = BLOCK_SIZE * (blockNum - 1);
        }
        int spacing = startIndex2 - startIndex1;
        for (int i = startIndex1; i < endIndex1; i++) {
            int block2I = i + spacing;
            byte t = content[i];
            content[i] = content[block2I];
            content[block2I] = t;
        }
        return content;
    }

    /**
     * 生成加密秘钥
     *
     * @return
     */
    private static SecretKeySpec getSecretKey(final String key, String algorithm) {
        // 返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            String array[] = algorithm.split("/");
            String keyAlgorithm = array[0];
            kg = KeyGenerator.getInstance(array[0]);
            // AES 要求密钥长度为 128
            kg.init(128, new SecureRandom(key.getBytes()));
            // 生成一个密钥
            SecretKey secretKey = kg.generateKey();
            return new SecretKeySpec(secretKey.getEncoded(), keyAlgorithm);// 转换为AES专用密钥
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Cipher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static Boolean isEnable = null;

    public static boolean isEnable() {
        if (isEnable != null) {
            return isEnable;
        }
        String encryptEnable = BeeClientConfiguration.getLocalProperies().getProperty(Constants.CONFIG_ENCRYPT_KEY);
        if (encryptEnable == null) {
            ClassPathResource bootstarterClass = new ClassPathResource("com/arcvideo/bee/boot/BeeStarter.class");
            try {
                byte[] bootstarterClassBytes = IOUtils.readAllBytes(bootstarterClass.getInputStream());
                bootstarterClassBytes = CipherUtil.decryptc(bootstarterClassBytes);
                if (bootstarterClassBytes == null) {
                    encryptEnable = "false";
                } else {
                    encryptEnable = "true";
                }
            } catch (IOException e1) {
                encryptEnable = "true";
            }
        }
        isEnable = StringUtil.equalsIgnoreCase(encryptEnable, "true");
        return isEnable;
    }
}