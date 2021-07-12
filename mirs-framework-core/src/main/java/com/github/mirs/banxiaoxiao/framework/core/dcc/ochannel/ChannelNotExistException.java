package com.github.mirs.banxiaoxiao.framework.core.dcc.ochannel;


/**
 * @author zcy 2019年3月18日
 */
public class ChannelNotExistException extends SendSignalException {

    /** */
    private static final long serialVersionUID = -6560834649071975904L;
    
    public ChannelNotExistException(String msg) {
        super(msg);
    }
    
    public ChannelNotExistException(Throwable e) {
        super(e);
    }
    
    public ChannelNotExistException(String msg, Throwable e) {
        super(msg, e);
    }
}
