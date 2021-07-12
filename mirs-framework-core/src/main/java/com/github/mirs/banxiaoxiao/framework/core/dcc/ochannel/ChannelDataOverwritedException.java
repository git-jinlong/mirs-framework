package com.github.mirs.banxiaoxiao.framework.core.dcc.ochannel;

/**
 * @author zcy 2019年3月18日
 */
public class ChannelDataOverwritedException extends ChannelException {

    /** */
    private static final long serialVersionUID = 2454817522444768158L;

    public ChannelDataOverwritedException(String msg) {
        super(msg);
    }

    public ChannelDataOverwritedException(Throwable e) {
        super(e);
    }

    public ChannelDataOverwritedException(String msg, Throwable e) {
        super(msg, e);
    }
}
