package com.github.mirs.banxiaoxiao.framework.core.drpc.group;
import java.io.Serializable;

/**
 * @author erxiao 2017年8月18日
 */
public class NodeEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2871041075757914863L;

	/** 服务名 */
	String serviceName;

	String nodeHost;

	int nodePort;

	EventType eventType;

	String clusterId;
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getNodeHost() {
		return nodeHost;
	}

	public void setNodeHost(String nodeHost) {
		this.nodeHost = nodeHost;
	}

	public int getNodePort() {
		return nodePort;
	}

	public void setNodePort(int nodePort) {
		this.nodePort = nodePort;
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