package com.github.mirs.banxiaoxiao.framework.core.boot;


/**
 * @author zcy 2019年4月15日
 */
public class InitializeException extends RuntimeException {

    /** */
    private static final long serialVersionUID = 1789473797358416376L;
    
    public InitializeException(String msg) {
        super(msg);
    }

    public InitializeException(String msg, Throwable e) {
        super(msg, e);
    }
}
