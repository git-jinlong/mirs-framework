package com.github.mirs.banxiaoxiao.framework.core.drpc;

import org.apache.dubbo.config.ServiceConfig;

import com.github.mirs.banxiaoxiao.framework.core.drpc.annotation.DS;

/**
 * @author zcy 2019年3月21日
 */
public class DServiceConfig<T> extends AbstractConfig {

    /** */
    private static final long serialVersionUID = -593979469730631402L;

    private ServiceConfig<T> config;

    public DServiceConfig() {
        super();
        this.config = new ServiceConfig<T>();
    }

    public DServiceConfig(DS dservice) {
        super(dservice.group(), dservice.groupLoader(), dservice.config());
        this.config = new ServiceConfig<T>();
    }

    public void setInterface(String interfaceName) {
        this.config.setInterface(interfaceName);
    }
    
    public void setInterface(Class<?> interfaceClass) {
        this.config.setInterface(interfaceClass);
    }

    public ServiceConfig<T> getConfig() {
        return config;
    }

    public boolean isExport() {
        return this.config.isExported();
    }

    public void export() {
        setDubboConfigValue(this.config);
        this.config.export();
    }

    public void unexport() {
        this.config.unexport();
    }

    public T getRef() {
        return this.config.getRef();
    }

    public void setRef(T ref) {
        this.config.setRef(ref);
    }
}
