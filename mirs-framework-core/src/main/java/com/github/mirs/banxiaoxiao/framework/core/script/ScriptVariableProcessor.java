package com.github.mirs.banxiaoxiao.framework.core.script;

/**
 * @author zcy 2019年5月23日
 */
public interface ScriptVariableProcessor {

    /**
     * 传入的变量key不包含${}
     * 
     * @param var
     * @return
     */
    public Object process(String var);
}
