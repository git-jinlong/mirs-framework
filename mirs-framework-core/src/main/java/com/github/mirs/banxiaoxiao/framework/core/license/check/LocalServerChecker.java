package com.github.mirs.banxiaoxiao.framework.core.license.check;

import com.github.mirs.banxiaoxiao.framework.common.util.NetworkUtil;
import com.github.mirs.banxiaoxiao.framework.core.license.License;
import com.github.mirs.banxiaoxiao.framework.core.license.LicenseException;

/**
 * 检查服务器是否授权
 *
 * @author zcy 2019年6月27日
 */
public class LocalServerChecker extends BaseChecker {

    public final String CHECKER_ENABLE_KEY = "checker.enable.server";

    @Override
    public void check(License license) throws LicenseException {
        boolean enable = checkerEnable(CHECKER_ENABLE_KEY, license, true);
        if (!enable) {
            return;
        }
        String localUid = license.getLocalUid();
        if (license.getUuids() == null || !license.getUuids().contains(localUid)) {
            throw new LicenseException("server [" + localUid + "," + NetworkUtil.getLocalHost() + "] unauthorized");
        }
    }

}
