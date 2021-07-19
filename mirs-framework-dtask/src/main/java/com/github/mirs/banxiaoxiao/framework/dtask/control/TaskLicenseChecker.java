package com.github.mirs.banxiaoxiao.framework.dtask.control;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;
import com.github.mirs.banxiaoxiao.framework.core.dcc.SingleDccClientHelper;
import com.github.mirs.banxiaoxiao.framework.core.event.EventPublishHelper;
import com.github.mirs.banxiaoxiao.framework.core.license.License;
import com.github.mirs.banxiaoxiao.framework.core.log.TComLogs;
import com.github.mirs.banxiaoxiao.framework.dtask.Constants;
import com.github.mirs.banxiaoxiao.framework.dtask.event.DTaskEventExceedBudget;

import java.util.List;

public class TaskLicenseChecker {

    public static final String CHECK_KEY = "bee.dtask.max.%s";

    /**
     * 验证是否满足授权的限制
     *
     * @return 符合License返回true，否则false
     **/
    /*public static boolean check(String taskCode,int taskSize){
        try{
            License.get().checkGreaterAndEqu(String.format(CHECK_KEY,taskCode),taskSize);//验证授权数值是否 >= taskSize，如果 小于 taskSize就抛异常
        }catch(LicenseException e){
            int licenseSize = License.get().getInt(String.format(CHECK_KEY,taskCode));
            TComLogs.error("license authorized size {} is less than task size {}",licenseSize,taskSize);
            EventPublishHelper.get().notice(new DTaskEventExceedBudget(licenseSize));
            return false;
        }
        TComLogs.debug("license authorized size {} >= task size {}");
        return true;
    }*/
    public static boolean check(String taskCode, int taskSize) {
        Long licenseSize = License.get().getLong(String.format(CHECK_KEY, taskCode), null);
        if (licenseSize == null || licenseSize < 0L) {
            TComLogs.debug("license authorized size is no limited. now task size is {} .", taskSize);
            return true;
        }
        if (licenseSize <= (long) taskSize) {//如果是0，表示不允许开启
            TComLogs.error("license authorized size {} is less than task size {} .", licenseSize, taskSize);
            EventPublishHelper.get().notice(new DTaskEventExceedBudget(licenseSize.intValue()));
            return false;
        }
        TComLogs.debug("license authorized size {}, now task size is {} .", licenseSize, taskSize);
        return true;
    }

    //TODO::扩展别的检验时......职责链模式？？

    private static final String DERAULT_TASK_CODE = "launch";//先默认是抓拍任务

    public static int getCurrentTaskNum() {
        return getCurrentTaskNUm(DERAULT_TASK_CODE);
    }

    public static int getCurrentTaskNUm(String taskCode) {
        List<String> children = SingleDccClientHelper.get().getChildren(String.format(Constants.KEY_FORMAT_TASKPROXY, taskCode));//TODO::需要改成基于AbstractDccApp
        if (children == null) {
            return 0;
        }
        return children.size();
    }

    public static Boolean checkRunning(String taskId) {
        return checkRunning(DERAULT_TASK_CODE, taskId);
    }

    public static Boolean checkRunning(String taskCode, String taskId) {
        List<String> children = SingleDccClientHelper.get().getChildren(String.format(Constants.KEY_FORMAT_TASKPROXY, taskCode));
        if (children == null || children.size() == 0) {
            return false;
        }
        for (String child : children) {
            if (StringUtil.equals(child, taskId)) {
                return true;
            }
        }
        return false;
    }
}
