package com.github.mirs.banxiaoxiao.framework.core.dcc;

/**
 * @author zcy 2018年10月9日
 */
public class DccConnectException extends DccException {

    /** */
    private static final long serialVersionUID = 8671858944393721871L;

    public DccConnectException(String msg) {
        super(msg);
    }

    public DccConnectException(Throwable e) {
        super(e);
    }

    public DccConnectException(String msg, Throwable e) {
        super(msg, e);
    }
}
