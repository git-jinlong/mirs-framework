package com.github.mirs.banxiaoxiao.framework.core.license;

/**
 * @author zcy 2019年6月3日
 */
public interface LicenseChecker {

    public void check(License license) throws LicenseException;
}
