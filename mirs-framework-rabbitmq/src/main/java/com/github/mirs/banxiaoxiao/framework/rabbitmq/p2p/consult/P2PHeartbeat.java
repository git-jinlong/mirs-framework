package com.github.mirs.banxiaoxiao.framework.rabbitmq.p2p.consult;

import com.github.mirs.banxiaoxiao.framework.common.util.StringUtil;

import java.io.Serializable;
import java.util.List;

/**
 * @author zcy 2019年7月19日
 */
public class P2PHeartbeat implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2786673060649428176L;

    private long time;

    private String host;

    private boolean isLeader;

    private List<String> supportMsgTypes;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean isLeader) {
        this.isLeader = isLeader;
    }

    public List<String> getSupportMsgTypes() {
        return supportMsgTypes;
    }

    public void setSupportMsgTypes(List<String> supportMsgTypes) {
        this.supportMsgTypes = supportMsgTypes;
    }

    public P2PHeartbeat() {
    }

    public P2PHeartbeat(String host) {
        this.host = host;
    }

    @Override
    public int hashCode() {
        return getHost() == null ? 0 : getHost().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof P2PHeartbeat)) {
            return false;
        }
        String selfHost = getHost();
        String otherHost = ((P2PHeartbeat) obj).getHost();
        return StringUtil.equals(selfHost, otherHost);
    }
}
