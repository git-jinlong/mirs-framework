package com.github.mirs.banxiaoxiao.framework.core.script;

/**
 * @author zcy 2019年5月23日
 */
public class ScriptHelper {

    private static Script script;

    public static Script getJavaScript() {
        if (script == null) {
            throw new NullPointerException("script not instantiated ");
        }
        return script;
    }

    public static void inject(Script script) {
        if (script == null) {
            throw new NullPointerException("javaScript can not be null.");
        }
        ScriptHelper.script = script;
    }
}
