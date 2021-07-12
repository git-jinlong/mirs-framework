package com.github.mirs.banxiaoxiao.framework.core.drpc;

import com.github.mirs.banxiaoxiao.framework.core.cross.annotation.CDS;
import org.apache.dubbo.config.ReferenceConfig;
import com.github.mirs.banxiaoxiao.framework.core.drpc.annotation.DS;

/**
 * @author zcy 2019年3月21日
 */
public class DReferenceConfig<T> extends AbstractConfig {

    /** */
    private static final long serialVersionUID = -3277477572519099113L;

    private ReferenceConfig<T> config;

    private boolean geted = false;

    public DReferenceConfig() {
        super();
        this.config = new ReferenceConfig<T>();
        setDefaultConfig();
    }

    public DReferenceConfig(DS dservice) {
        this(dservice, null);
    }

    public DReferenceConfig(DS dservice, Class<?> interfaceClass) {
        this(dservice.config(), interfaceClass, dservice.group(), dservice.groupLoader());
    }

    public DReferenceConfig(CDS dservice) {
        this(dservice, null);
    }

    public DReferenceConfig(CDS dservice, Class<?> interfaceClass) {
        this(dservice.config(), interfaceClass, null, null);
    }

    public DReferenceConfig(String config, Class<?> interfaceClass, String group, Class<?> groupLoader) {
        super(group, groupLoader, config);
        this.config = new ReferenceConfig<T>();
        setInterface(interfaceClass);
        setDefaultConfig();
    }

    private void setDefaultConfig() {
        this.config.setCheck(false);
        this.config.setLazy(true);
    }

    public ReferenceConfig<T> getConfig() {
        return config;
    }

    public void setInterface(Class<?> interfaceClass) {
        this.config.setInterface(interfaceClass);
    }

    public void setInterface(String interfaceName) {
        this.config.setInterface(interfaceName);
    }

    public synchronized T get() {
        if (!geted) {
            setDubboConfigValue(this.config);
            geted = true;
        }
        return this.config.get();
    }

    /**
     * 配置变化后刷新一下，然后重新{@link #get()}一把
     */
    public synchronized void refresh() {
        if (this.config != null) {
            this.config.destroy();
            this.config = null;
        }
        this.config = new ReferenceConfig<T>();
        setDubboConfigValue(this.config);
    }

    public synchronized void destroy() {
        if (this.config != null) {
            this.config.destroy();
        }
    }
}
