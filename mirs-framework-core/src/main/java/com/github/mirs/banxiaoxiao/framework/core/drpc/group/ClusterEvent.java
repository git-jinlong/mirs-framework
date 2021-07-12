package com.github.mirs.banxiaoxiao.framework.core.drpc.group;
import java.io.Serializable;

/**
 * 服务集群事件
 * 
 * @author erxiao 2017年8月18日
 */
public class ClusterEvent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6086466362033505716L;

	/** 服务名 */
	String serviceName;

	EventType eventType;

	String clusterId;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

}