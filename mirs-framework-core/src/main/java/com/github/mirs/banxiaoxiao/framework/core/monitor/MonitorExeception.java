package com.github.mirs.banxiaoxiao.framework.core.monitor;

public class MonitorExeception extends RuntimeException {

    private static final long serialVersionUID = -6019374608282753883L;

    public MonitorExeception(){

    }

    public MonitorExeception(String msg){
        super(msg);
    }

    public MonitorExeception(Throwable e){
        super(e);
    }

    public MonitorExeception(String msg, Throwable e){
        super(msg,e);
    }
}
