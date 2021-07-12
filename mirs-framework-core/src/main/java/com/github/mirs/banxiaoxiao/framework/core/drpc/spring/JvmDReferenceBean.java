package com.github.mirs.banxiaoxiao.framework.core.drpc.spring;

import org.apache.dubbo.config.support.Parameter;

/**
 * @author zcy 2020年4月26日
 * @param <T>
 */
public class JvmDReferenceBean<T> extends DReferenceBean<T> {

    /**
     * 
     */
    private static final long serialVersionUID = -4948290198205323958L;

    private T jvmObject;

    public JvmDReferenceBean() {
    }

    public JvmDReferenceBean(T jvmObject) {
        this.jvmObject = jvmObject;
    }

    public void setJvmObject(T jvmObject) {
        this.jvmObject = jvmObject;
    }
    
    @Override
    public T getObject() throws Exception {
        return this.jvmObject;
    }

    @Override
    public Class<?> getObjectType() {
        return jvmObject.getClass();
    }

    public void setConfig(String config) {
        super.setRpcConfig(config);
    }
    
    @Override
    @Parameter(excluded = true)
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        
    }
}
