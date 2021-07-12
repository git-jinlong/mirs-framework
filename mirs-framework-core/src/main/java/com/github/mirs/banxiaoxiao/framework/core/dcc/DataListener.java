package com.github.mirs.banxiaoxiao.framework.core.dcc;

/**
 * <pre>
 * 监听的节点数据发生变化时的回调接口
 * 监听路径暂时不支持继承至父类。如：
 * DccClient.registNodeListener("/arc", listener);
 * 当 "/arc/cam1" 节点数据发生变化时不会通知监听"/arc"节点的监听器
 * </pre>
 * @author zcy
 * 2018年8月23日
 */
public interface DataListener<T> {

	public void onUpdate(String path, T data);

}
