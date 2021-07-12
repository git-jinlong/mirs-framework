package com.github.mirs.banxiaoxiao.framework.core.drpc;

/**
 * @author zcy 2019年3月21日
 */
public class ConfigException extends RuntimeException {

    /** */
    private static final long serialVersionUID = 8803431880321527131L;

    public ConfigException(String msg) {
        super(msg);
    }

    public ConfigException(Throwable e) {
        super(e);
    }

    public ConfigException(String msg, Throwable e) {
        super(msg, e);
    }
}
