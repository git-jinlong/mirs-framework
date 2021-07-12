package com.github.mirs.banxiaoxiao.framework.core.dcc.ochannel;


/**
 * 信令还未被消费
 * @author zcy 2019年3月15日
 */
public class SignalNotConsumeException extends SendSignalException {

    /** */
    private static final long serialVersionUID = -8497799937768284763L;
    
    public SignalNotConsumeException(String msg) {
        super(msg);
    }
    
    public SignalNotConsumeException(Exception e) {
        super(e);
    }
    
    public SignalNotConsumeException(Throwable e) {
        super(e);
    }
    
    public SignalNotConsumeException(String msg, Exception e) {
        super(msg, e);
    }
}
