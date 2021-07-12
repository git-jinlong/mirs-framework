package com.github.mirs.banxiaoxiao.framework.core.woodpecker.vpipe;

import java.util.List;

import com.github.mirs.banxiaoxiao.framework.core.drpc.spring.DServiceBean;

public class DrpcProcessExporter extends DServiceBean<CommandProcess> implements CommandProcess {

    /** */
    private static final long serialVersionUID = -7105135970241010612L;

    private CommandProcess target;

    public DrpcProcessExporter(CommandProcess target, String host) {
        super();
        this.target = target;
        super.setRef(target);
        super.setInterface(CommandProcess.class);
        setGroup(host);
    }

    @Override
    public List<String> invoke(Command command) {
        return this.target.invoke(command);
    }
}
