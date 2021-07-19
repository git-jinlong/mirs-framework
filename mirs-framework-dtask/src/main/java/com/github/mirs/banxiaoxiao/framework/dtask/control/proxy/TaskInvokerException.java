package com.github.mirs.banxiaoxiao.framework.dtask.control.proxy;

import com.github.mirs.banxiaoxiao.framework.dtask.TaskException;

/**
 * @author zcy 2019年5月30日
 */
public class TaskInvokerException extends TaskException {

    /** */
    private static final long serialVersionUID = 7950747973284497266L;

    public TaskInvokerException() {
    }

    public TaskInvokerException(String msg) {
        super(msg);
    }

    public TaskInvokerException(Throwable e) {
        super(e);
    }

    public TaskInvokerException(String msg, Throwable e) {
        super(msg, e);
    }
}
