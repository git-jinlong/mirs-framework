package com.github.mirs.banxiaoxiao.framework.dtask.event;

public class DTaskEventExceedBudget extends DTaskEvent{

    private static final long serialVersionUID = -1773244533526375528L;

    private int budget;//license里的设定值

    public DTaskEventExceedBudget(int budget){
        super();
        this.budget = budget;
    }

    protected String describe(){
        return "task count can't be greater than "+budget;
    }
}
