package com.github.mirs.banxiaoxiao.framework.dtask.conf.mysql;

import com.github.mirs.banxiaoxiao.framework.dtask.TaskException;

/**
 * @author zcy 2019年5月29日
 */
public class NotFoundTargetConfigMethod extends TaskException {

    /** */
    private static final long serialVersionUID = -4553215739557901849L;

    public NotFoundTargetConfigMethod() {
    }

    public NotFoundTargetConfigMethod(String msg) {
        super(msg);
    }
    
    public NotFoundTargetConfigMethod(Throwable e) {
        super(e);
    }

    public NotFoundTargetConfigMethod(String msg, Throwable e) {
        super(msg, e);
    }
}
