package com.github.mirs.banxiaoxiao.framework.core.drpc.group.monitor;
/**
 * @author erxiao 2017年8月18日
 */
public interface ClusterListener {

	/**
	 * <p>
	 * 服务集群状态变更事件通知，服务集群事件必定会伴随NodeEvent事件。<br> 
	 * 如果集群先启动，ClusterListener后启动，也会将集群Started事件通知到Listener。<br>
	 * ClusterEvent 的clusterId对应group service的group name,
	 * 目前rpc层采用dubbo+zookeeper，grop name由服务提供者自行添加了前缀，
	 * 如: search_worker_01。listener请根据自己的需求踢去相应的id标识
	 * </p>
	 * @param clusterEvent
	 */
	public void onClusterChanage(ClusterEvent clusterEvent);

	/**
	 * 服务节点状态变更事件通知
	 * 
	 * @param clusterEvent
	 */
	public void onNodeChanage(NodeEvent nodeEvent);
}