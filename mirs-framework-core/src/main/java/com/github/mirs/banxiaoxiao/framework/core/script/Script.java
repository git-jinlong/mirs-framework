package com.github.mirs.banxiaoxiao.framework.core.script;


/**
 * 支持脚本语法，在jdk script engine基础上提供基于配置上下文，以便脚本直接获取配置key的值。配置上下文变量key以 ${propertyKey} 表示
 * 
 * @author zcy 2019年5月23日
 */
public interface Script {

    /**
     * 执行一串脚本
     * 
     * @param script
     * @return
     */
    public Object eval(String script) throws ScriptException;

    /**
     * @param script
     * @param processor
     * @return
     */
    public Object eval(String script, ScriptVariableProcessor processor) throws ScriptException;
}
