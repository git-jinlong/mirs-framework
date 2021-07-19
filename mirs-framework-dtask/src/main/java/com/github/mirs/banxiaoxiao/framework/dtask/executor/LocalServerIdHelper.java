package com.github.mirs.banxiaoxiao.framework.dtask.executor;

import com.github.mirs.banxiaoxiao.framework.common.util.NetworkUtil;

/**
 * @author zcy 2019年5月31日
 */
public class LocalServerIdHelper {

    public static String getServerId() {
        return NetworkUtil.getLocalHost();
    }
}
