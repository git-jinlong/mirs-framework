package com.github.mirs.banxiaoxiao.framework.rabbitmq;

/**
 * @author zcy 2019年6月19日
 */
public enum MsgChannelType {
    /** 队列模式 */
    QUEUE(0),
    /** 广播模式 */
    BROADCAST(1),
    /** P2P模式 */
    P2P(2),
    /** RPC模式 */
    RPC(3);

    int value;

    MsgChannelType(int v) {
        this.value = v;
    }

    public int getValue() {
        return this.value;
    }
}
