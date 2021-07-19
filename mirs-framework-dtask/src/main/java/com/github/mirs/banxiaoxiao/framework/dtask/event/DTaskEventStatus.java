package com.github.mirs.banxiaoxiao.framework.dtask.event;

public class DTaskEventStatus extends DTaskEvent{

    private Status status;

    public DTaskEventStatus(Status status){
        super();
        this.status = status;
    }

    public enum Status{
        RUNNING, STOPPED;
    }
}
