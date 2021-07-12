package com.github.mirs.banxiaoxiao.framework.core.dcc;

/**
 * @author zcy 2019年4月15日
 */
public class AbstractDcc {

    transient private DccClient dccClient;

    public AbstractDcc() {
    }

    public AbstractDcc(DccClient dccClient) {
        this.dccClient = dccClient;
    }

    public DccClient getDccClient() {
        DccClient dccClient = this.dccClient;
        if (dccClient == null) {
            dccClient = SingleDccClientHelper.get();
            this.dccClient = dccClient;
        }
        if (dccClient == null) {
            throw new IllegalArgumentException("dcc client not found");
        }
        return dccClient;
    }

    public void setDccClient(DccClient dccClient) {
        this.dccClient = dccClient;
    }
}
