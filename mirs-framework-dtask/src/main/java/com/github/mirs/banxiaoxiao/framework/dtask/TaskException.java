package com.github.mirs.banxiaoxiao.framework.dtask;

/**
 * @author zcy 2019年4月29日
 */
public class TaskException extends RuntimeException {

    /** */
    private static final long serialVersionUID = -3449154376717239096L;

    public TaskException() {
    }

    public TaskException(String msg) {
        super(msg);
    }

    public TaskException(Throwable e) {
        super(e);
    }

    public TaskException(String msg, Throwable e) {
        super(msg, e);
    }
}
