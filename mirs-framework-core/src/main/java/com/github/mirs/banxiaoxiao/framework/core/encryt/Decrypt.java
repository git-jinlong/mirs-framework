package com.github.mirs.banxiaoxiao.framework.core.encryt;

import org.fusesource.hawtjni.runtime.Library;

/**
 * @author zcy 2019年9月11日
 */
public class Decrypt {

    private static Library decryptLibrary = new Library("encrypt", Encrypt.class);
    static {
        decryptLibrary.load();
    }

    public static final native byte[] decrypt(byte[] _buf);
}
