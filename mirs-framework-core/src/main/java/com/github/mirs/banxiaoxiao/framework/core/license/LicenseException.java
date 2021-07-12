package com.github.mirs.banxiaoxiao.framework.core.license;

/**
 * @author zcy 2019年6月3日
 */
public class LicenseException extends RuntimeException {

    /** */
    private static final long serialVersionUID = -7118268174800267817L;

    public LicenseException() {
    }

    public LicenseException(String msg) {
        super(msg);
    }

    public LicenseException(Throwable e) {
        super(e);
    }

    public LicenseException(String msg, Throwable e) {
        super(msg, e);
    }
}