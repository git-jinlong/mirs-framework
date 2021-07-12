package com.github.mirs.banxiaoxiao.framework.core.cross;

import java.io.Serializable;

/**
 * 网络信息，比如：杭州市***项目，公安信息内网、公安视频专网就定义为两个网络
 * @author zcy 2019年9月3日
 */
public class Net implements Serializable {

    /** */
    private static final long serialVersionUID = -6171280400714899963L;

    private String id;

    private String name;

    private String host;

    private int port;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
