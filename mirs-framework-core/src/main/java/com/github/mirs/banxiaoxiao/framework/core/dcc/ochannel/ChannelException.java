package com.github.mirs.banxiaoxiao.framework.core.dcc.ochannel;


/**
 * @author zcy 2019年3月18日
 */
public class ChannelException extends Exception {

    /** */
    private static final long serialVersionUID = -8497799937768284763L;
    
    public ChannelException(String msg) {
        super(msg);
    }
    
    public ChannelException(Throwable e) {
        super(e);
    }
    
    public ChannelException(String msg, Throwable e) {
        super(msg, e);
    }
}
