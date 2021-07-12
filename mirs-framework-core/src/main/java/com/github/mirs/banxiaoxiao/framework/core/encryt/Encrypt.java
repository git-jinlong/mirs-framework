package com.github.mirs.banxiaoxiao.framework.core.encryt;

import org.fusesource.hawtjni.runtime.Library;

public class Encrypt {

    private static Library encryptLibrary = new Library("encrypt", Encrypt.class);
    static {
        encryptLibrary.load();
    }

    public static final native byte[] encrypt(byte[] _buf);
}