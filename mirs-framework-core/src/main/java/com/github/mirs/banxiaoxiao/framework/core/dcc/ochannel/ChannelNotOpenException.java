package com.github.mirs.banxiaoxiao.framework.core.dcc.ochannel;


/**
 * @author zcy 2019年3月18日
 */
public class ChannelNotOpenException extends SendSignalException {

    /** */
    private static final long serialVersionUID = -6560834649071975904L;
    
    public ChannelNotOpenException(String msg) {
        super(msg);
    }
    
    public ChannelNotOpenException(Throwable e) {
        super(e);
    }
    
    public ChannelNotOpenException(String msg, Throwable e) {
        super(msg, e);
    }
}
