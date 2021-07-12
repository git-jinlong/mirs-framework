package com.github.mirs.banxiaoxiao.framework.core.monitor.app.event;

import java.util.List;

/**
 * @author zcy 2018年9月21日
 */
public class AppInstanceRefreshEvent extends AppEvent {

    /** */
    private static final long serialVersionUID = -6742599314166620480L;

    private List<String> instances;

    public List<String> getInstances() {
        return instances;
    }

    public void setInstances(List<String> instances) {
        this.instances = instances;
    }

    protected String describe() {
        return getAppInfo() + "," + instances.size();
    }
}
