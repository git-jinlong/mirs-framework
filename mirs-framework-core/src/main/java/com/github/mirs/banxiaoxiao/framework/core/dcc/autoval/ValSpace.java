package com.github.mirs.banxiaoxiao.framework.core.dcc.autoval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author zcy 2018年9月14日
 */
public abstract class ValSpace extends Properties {

    /** */
    private static final long serialVersionUID = -1708480498847656878L;

    protected transient Logger logger = LoggerFactory.getLogger(getClass());

    private transient List<Valistener> listeners = new ArrayList<Valistener>();

    public ValSpace() {
    }

    public abstract String getNamespace();

    /**
     * put and notice update
     * @param key
     * @param data
     */
    public void put(String key, Object data) {
        super.put(key, data);
        noticeUpdate(key, data);
    }
    
    public void putNoNotice(String key, Object data) {
        super.put(key, data);
    }

    /**
     * 命名空间配置优先级，越小优先级越高
     * 
     * @return
     */
    public int getPriority() {
        return 350;
    }

    public void registerListener(Valistener listener) {
        if (listener != null && !this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    protected synchronized void noticeUpdate(String key, Object data) {
        for (Valistener listener : listeners) {
            try {
                listener.onChange(getNamespace(), key, data);
            } catch (Exception e) {
                logger.error("notice data change fail ", e);
            }
        }
    }
}
