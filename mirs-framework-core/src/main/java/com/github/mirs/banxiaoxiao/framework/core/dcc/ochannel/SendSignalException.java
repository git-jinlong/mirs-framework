package com.github.mirs.banxiaoxiao.framework.core.dcc.ochannel;


/**
 * @author zcy 2019年3月18日
 */
public class SendSignalException extends ChannelException {

    /** */
    private static final long serialVersionUID = -8497799937768284763L;
    
    public SendSignalException(String msg) {
        super(msg);
    }
    
    public SendSignalException(Throwable e) {
        super(e);
    }
    
    public SendSignalException(String msg, Throwable e) {
        super(msg, e);
    }
}
