package com.github.mirs.banxiaoxiao.framework.core.dcc;

/**
 * @author zcy 2019年4月2日
 */
public class BadVersionException extends DccException {

    /** */
    private static final long serialVersionUID = 3255297377563342390L;

    public BadVersionException(String msg) {
        super(msg);
    }

    public BadVersionException(Throwable e) {
        super(e);
    }

    public BadVersionException(String msg, Throwable e) {
        super(msg, e);
    }
}
