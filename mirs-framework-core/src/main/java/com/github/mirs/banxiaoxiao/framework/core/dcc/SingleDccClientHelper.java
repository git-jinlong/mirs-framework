package com.github.mirs.banxiaoxiao.framework.core.dcc;

/**
 * @author zcy 2019年3月12日
 */
public class SingleDccClientHelper {

    private static DccClient dccClient;

    public static DccClient get() {
        if (dccClient == null) {
            throw new NullPointerException("dcc client not instantiated, please add @DccClientEnable");
        }
        return dccClient;
    }

    public static void inject(DccClient dccClient) {
        if (dccClient == null) {
            throw new NullPointerException("dcc client can not be null.");
        }
        SingleDccClientHelper.dccClient = dccClient;
    }
}
