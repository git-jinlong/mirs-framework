package com.github.mirs.banxiaoxiao.framework.core.dcc;

public class NodeNotExistException extends DccException {

    /** */
    private static final long serialVersionUID = 8998860628088973049L;

    public NodeNotExistException(String msg) {
        super(msg);
    }

    public NodeNotExistException(Throwable e) {
        super(e);
    }

    public NodeNotExistException(String msg, Throwable e) {
        super(msg, e);
    }
}
