package com.github.mirs.banxiaoxiao.framework.core.dcc;

/**
 * @author zcy 2018年8月23日
 */
public class DccException extends RuntimeException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 7682350078637635354L;

    public DccException(String msg) {
        super(msg);
    }

    public DccException(Throwable e) {
        super(e);
    }

    public DccException(String msg, Throwable e) {
        super(msg, e);
    }
}
