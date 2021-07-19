package com.github.mirs.banxiaoxiao.framework.dtask.seres;

import com.github.mirs.banxiaoxiao.framework.common.util.MultiTypeValMap;
import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

/**
 * 可运行任务的服务器资源
 *
 * @author zcy 2019年4月29日
 */
public class ServerResource extends MultiTypeValMap {

    /**
     *
     */
    private static final long serialVersionUID = 9036068494213877607L;

    private String id;

    private String ip;

    public ServerResource() {
    }

    public ServerResource(String id, String ip) {
        super();
        this.id = id;
        this.ip = ip;
    }

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

    @Override
    public int hashCode() {
        return this.id == null ? 0 : this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ServerResource)) {
            return false;
        }
        ServerResource self = this;
        ServerResource other = (ServerResource) obj;
        return StringUtil.equals(self.getId(), other.getId());
    }

    public String toString() {
        return getId() + " " + getIp();
    }
}
