package com.github.mirs.banxiaoxiao.framework.core.monitor.machine;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zcy 2019年4月29日
 */
public class MachineResource implements Serializable {

    /** */
    private static final long serialVersionUID = 4219229222174784003L;

    private String id;

    private String ip;

    private Map<String, Object> attributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
