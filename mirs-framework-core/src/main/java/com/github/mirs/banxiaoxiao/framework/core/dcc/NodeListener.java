package com.github.mirs.banxiaoxiao.framework.core.dcc;

import java.util.List;


/**
 * <pre>
 * 节点监听器，当监听节点及其子节点发生变化时的回调。
 * 节点名称是以监听根节点为相对路径的路径名称。比如:
 * 1. 先注册节点监听器
 *    NodeListener listener = new MyNodeListener();
 *    DccClient.registNodeListener("/arc", listener);
 * 2. 有客户端创建了arc的一个子节点为/arc/node1
 * 3. 监听器将收到nodeName = "node1" 的回调事件
 * 
 * </pre>
 * @author zcy
 * 2018年8月23日
 */
public interface NodeListener {

	/**
	 * 监听节点被删除时回调
	 */
	public void onDestroy(String nodeName);
	
	/**
	 * <pre>
	 * 监听节点被创建时回调. 
	 * DccClient.registNodeListener("arc", listener);
	 * 当注册监听器时如果arc节点还不存在，监听器也会注册成功。在该节点被创建时会回调本方法
	 * </pre>
	 */
	public void onConstruct(String nodeName);
	
	/**
	 * @param children
	 */
	public void onRefreshChildren(List<String> children);
}
