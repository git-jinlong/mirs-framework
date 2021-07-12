package com.github.mirs.banxiaoxiao.framework.core.dcc.ochannel;

public interface SignalReceiver<S> {

    public void onSignal(S s);
    
}
