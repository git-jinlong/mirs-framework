package com.github.mirs.banxiaoxiao.framework.core.monitor.app.event;


/**
 * @author zcy 2018年9月21日
 */
public class AppInstaceStopEvent extends AppEvent {

    /** */
    private static final long serialVersionUID = -2572478075110709971L;

    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
