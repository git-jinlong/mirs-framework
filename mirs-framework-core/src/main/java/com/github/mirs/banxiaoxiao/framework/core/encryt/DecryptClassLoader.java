package com.github.mirs.banxiaoxiao.framework.core.encryt;

import com.github.mirs.banxiaoxiao.framework.common.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zcy 2019年9月11日
 */
public class DecryptClassLoader extends ClassLoader {

    public DecryptClassLoader(ClassLoader paramClassLoader) {
        super(paramClassLoader);
    }

    public Class<?> define(String className, byte[] bytes) {
        return super.defineClass(className, bytes, 0, bytes.length);
    }

    public boolean isExist(String className) {
        Class<?> clazz = super.findLoadedClass(className);
        return clazz != null;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream is = super.getResourceAsStream(name);
        if (is != null && name.endsWith(".class")) {
            try {
                byte[] bytes = IOUtils.readAllBytes(is);
                byte[] decryptBytes = CipherUtil.decryptc(bytes);
                if (decryptBytes == null) {
                    decryptBytes = bytes;
                }
                ByteArrayInputStream bais = new ByteArrayInputStream(decryptBytes);
                return bais;
            } catch (IOException e) {
                throw new ClassCastException("read byte fail, " + e.getMessage());
            }
        }
        return is;
    }
}
