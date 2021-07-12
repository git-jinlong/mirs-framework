package com.github.mirs.banxiaoxiao.framework.core.dcc.ochannel;

/**
 * <pre>
 * 信令信道，用于把一些简单的指令等数据发送到目标客户端上。用于并发不高的简单业务场景
 * </pre>
 * 
 * @author zcy 2019年3月13日
 */
public interface OneWayChannel<S> {

    /**
     * @return
     */
    public String id();

    /**
     * 
     */
    public void open() throws ChannelNotExistException;

    /**
     * 向通道内发送消息，如果发送失败则抛出异常
     * 
     * @param data
     * @param version
     * @throws ChannelException
     */
    public void send(S data, int version) throws ChannelException;

    /**
     * 销毁信道，如果信道内还有未消费的信令也会被删除
     */
    public void destory();

    /**
     * <pre>
     *   设置信令送达质量要求
     * </pre>
     * 
     * @param qos
     */
    public void setQos(int qos);

}
