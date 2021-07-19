package com.github.mirs.banxiaoxiao.framework.rabbitmq.mq;

public enum MqType {
	/*
	 * 任务模式
	 */
	TASK, 
	/*
	 * 发布订阅模式
	 */
	BROADCAST,
	/*
	 * 路由模式
	 */
	ROUTE, 
	/*
	 * 主题模式
	 */
	TOPIC;	
}
