package com.github.mirs.banxiaoxiao.framework.core.monitor;

import java.io.Serializable;

/**
 * @author zcy 2019年1月17日
 */
public class BaseEvent implements Serializable {

    /** */
    private static final long serialVersionUID = 6293498130866498094L;

    protected String describe() {
        return null;
    }

    public String toString() {
        return getClass().getSimpleName() + " [" + (describe() == null ? "" : describe()) + "]";
    }
}
