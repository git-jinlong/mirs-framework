package com.github.mirs.banxiaoxiao.framework.core.license.check;

import com.github.mirs.banxiaoxiao.framework.core.license.License;
import com.github.mirs.banxiaoxiao.framework.core.license.LicenseChecker;
import com.github.mirs.banxiaoxiao.framework.core.script.ScriptHelper;
import com.github.mirs.banxiaoxiao.framework.core.script.ScriptVariableProcessor;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

/**
 * @author zcy 2019年11月14日
 */
public abstract class BaseChecker implements LicenseChecker {

    /**
     * 计算目标key的值是否为true，如果key不存在或者脚本运行失败，则返回默认值 defaultEnable
     * 
     * @param key
     * @param license
     * @param defaultEnable
     * @return
     */
    protected boolean checkerEnable(String key, License license, boolean defaultEnable) {
        String enableScript = license.getString(key);
        if (StringUtil.isBlank(enableScript)) {
            return defaultEnable;
        }
        Object obj = ScriptHelper.getJavaScript().eval(enableScript, new ScriptVariableProcessor() {

            @Override
            public Object process(String var) {
                return license.getString(var);
            }
        });
        if (obj == null || (!obj.equals(false) && !obj.equals(true))) {
            return defaultEnable;
        }
        return (boolean) obj;
    }
}
