package com.github.mirs.banxiaoxiao.framework.core.dcc;

/**
 * @author zcy
 * 2018年8月24日
 */
public class DccProperty {
	
	private String zkHost;
	
	private int connectTimeout=3000;

	public String getZkHost() {
		return zkHost;
	}

	public void setZkHost(String zkHost) {
		this.zkHost = zkHost;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	
}
