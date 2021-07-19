package com.github.mirs.banxiaoxiao.framework.dtask.control.defofmax;

import com.github.mirs.banxiaoxiao.framework.core.dcc.AbstractDccApp;
import com.github.mirs.banxiaoxiao.framework.dtask.executor.LocalServerIdHelper;

public class CapacityWriter extends AbstractDccApp {

    public CapacityWriter(String taskCode) {
        super(String.format(CapacityRoot.PATH, taskCode));
        setClazz(Integer.class);
    }

    public void init(Integer capacity) {
        getDccClient().writeTempData(genChildPath(LocalServerIdHelper.getServerId()), capacity);
    }

}
