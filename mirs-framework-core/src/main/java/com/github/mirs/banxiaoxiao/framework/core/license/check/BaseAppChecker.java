package com.github.mirs.banxiaoxiao.framework.core.license.check;

import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.license.License;
import com.github.mirs.banxiaoxiao.framework.core.license.LicenseException;

/**
 * @author zcy 2019年11月14日
 */
public abstract class BaseAppChecker extends BaseChecker {

    private final String CHECKER_PRE_KEY = "checker.enable.app";

    @Override
    public void check(License license) throws LicenseException {
        String appName = BeeClientConfiguration.getLocalProperies().getAppName();
        boolean enable = checkerEnable(CHECKER_PRE_KEY, license, true);
        if (!enable) {
            return;
        }
        enable = checkerEnable(CHECKER_PRE_KEY + "." + appName, license, true);
        if (!enable) {
            return;
        }
        doCheck(license);
    }
    
    protected abstract void doCheck(License license) throws LicenseException;
}
