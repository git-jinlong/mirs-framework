package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq;

/**
 * @author erxiao 2017年2月5日
 * 
 */
public class ChannelProperty {
	private MqType requiredType=MqType.TOPIC;

	private boolean isAutoAck =true;
	private boolean durable=false;
	private boolean isQos=false;
	private String exchange="";
	//private String queue="";
	public MqType getRequiredType() {
		return requiredType;
	}
	public void setRequiredType(MqType requiredType) {
		this.requiredType = requiredType;
	}
	public boolean isAutoAck() {
		return isAutoAck;
	}
	public void setAutoAck(boolean isAutoAck) {
		this.isAutoAck = isAutoAck;
	}
	public boolean isDurable() {
		return durable;
	}
	public void setDurable(boolean durable) {
		this.durable = durable;
	}
	public boolean isQos() {
		return isQos;
	}
	public void setQos(boolean isQos) {
		this.isQos = isQos;
	}
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
//	public String getQueue() {
//		return queue;
//	}
//	public void setQueue(String queue) {
//		this.queue = queue;
//	}

	private static String stringFormat="exchange:%s,queue:%s,autoAck:%b,qos:%b,durable:%b,mqType:%s";
	
	public String toString(){
		return String.format(stringFormat, this.exchange,this.isAutoAck,this.isQos,this.durable,this.requiredType.toString());
	}

}
