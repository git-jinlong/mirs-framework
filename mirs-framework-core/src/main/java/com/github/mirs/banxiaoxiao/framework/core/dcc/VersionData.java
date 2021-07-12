package com.github.mirs.banxiaoxiao.framework.core.dcc;

import java.io.Serializable;

/**
 * @author zcy 2019年3月15日
 * @param <T>
 */
public class VersionData<T> implements Serializable {

    /** */
    private static final long serialVersionUID = -3485849406885708477L;

    private int version;

    private T data;

    public VersionData(int version, T data) {
        super();
        this.version = version;
        this.data = data;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
