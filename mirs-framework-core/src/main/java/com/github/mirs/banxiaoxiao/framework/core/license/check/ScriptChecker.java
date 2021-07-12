package com.github.mirs.banxiaoxiao.framework.core.license.check;

import java.util.Map.Entry;

import javax.annotation.Resource;

import com.github.mirs.banxiaoxiao.framework.core.license.License;
import com.github.mirs.banxiaoxiao.framework.core.license.LicenseChecker;
import com.github.mirs.banxiaoxiao.framework.core.license.LicenseException;
import com.github.mirs.banxiaoxiao.framework.core.script.Script;
import com.github.mirs.banxiaoxiao.framework.core.script.ScriptVariableProcessor;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

/**
 * @author zcy 2019年6月4日
 */
public class ScriptChecker implements LicenseChecker {

    public static final String LICENSE_PRE_KEY = "checker.script";

    @Resource
    private Script script;

    @Override
    public void check(License license) throws LicenseException {
        for (Entry<String, Object> entry : license.getVals().entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            if (key.trim().startsWith(LICENSE_PRE_KEY) && val != null && !StringUtil.isBlank(val.toString())) {
                checkScript(val.toString(), license);
            }
        }
    }

    protected Object eval(String scriptStr, License license) {
        Object obj = script.eval(scriptStr, new ScriptVariableProcessor() {

            @Override
            public Object process(String var) {
                return license.getString(var);
            }
        });
        return obj;
    }

    protected void checkScript(String scriptStr, License license) {
        Object obj = eval(scriptStr, license);
        if (obj == null || !obj.equals(true)) {
            throw new LicenseException("script condition " + scriptStr + " grant fail");
        }
    }
}
