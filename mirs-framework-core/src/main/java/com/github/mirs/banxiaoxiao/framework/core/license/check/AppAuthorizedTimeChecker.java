package com.github.mirs.banxiaoxiao.framework.core.license.check;

import com.github.mirs.banxiaoxiao.framework.core.config.BeeClientConfiguration;
import com.github.mirs.banxiaoxiao.framework.core.license.License;
import com.github.mirs.banxiaoxiao.framework.core.license.LicenseException;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;

import java.text.SimpleDateFormat;

/**
 * @author zcy 2019年6月3日
 */
public class AppAuthorizedTimeChecker extends BaseAppChecker {

    public static final String KEY_START_TIME = "startTime";

    public static final String KEY_END_TIME = "endTime";

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void doCheck(License license) throws LicenseException {
        String appName = BeeClientConfiguration.getLocalProperies().getAppName();
        String startTimeKey = appName + "." + KEY_START_TIME;
        String endTimeKey = appName + "." + KEY_END_TIME;
        String startTimeStr = license.getString(startTimeKey);
        String endTimeStr = license.getString(endTimeKey);
        long startTime = 0, endTime = 0;
        try {
            startTime = formatter.parse(startTimeStr).getTime();
            endTime = formatter.parse(endTimeStr).getTime();
        } catch (Exception e) {
            TComLogs.warn("invalid format [{} {}]", startTimeStr, endTimeStr);
        }
        long nowTime = System.currentTimeMillis();
        if (nowTime < startTime || nowTime > endTime) {
            throw new LicenseException(startTimeKey + "-" + endTimeKey + "[" + startTimeStr + "-" + endTimeStr + "] unauthorized");
        }
    }
}
