package com.github.mirs.banxiaoxiao.framework.core.drpc.group.monitor;
/**
 * @author erxiao 2017年8月18日
 */
public enum EventType {
	/**
	 * 1.集群已经启动事件，只要集群中有一个服务节点起来了就认为起来了  2.集群中的一个节点已启动
	 * */
	STARTED,

	/**
	 * 1.集群已停止事件，集群服务所有的节点都停止了就认为集群停止  2.集群中的一个节点已停止
	 * */
	STOPED
}