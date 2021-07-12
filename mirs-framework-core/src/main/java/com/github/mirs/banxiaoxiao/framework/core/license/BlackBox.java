package com.github.mirs.banxiaoxiao.framework.core.license;

import org.fusesource.hawtjni.runtime.Library;

/**
 * @author zcy 2019年6月3日
 */
public class BlackBox {

    private static Library decryptLibrary = new Library("checklic", BlackBox.class);
    static {
        decryptLibrary.load();
    }

    protected static final native byte[] decrypt(byte[] data);
    
    protected static final native String decodesystemid(byte[] infuffer);
}
