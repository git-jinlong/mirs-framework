package com.github.mirs.banxiaoxiao.framework.core.woodpecker.vpipe;

import com.github.mirs.banxiaoxiao.framework.common.util.UUID;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author zcy 2020年3月11日
 */
public class Msg {

    private String msgId;

    public Msg() {
        this.msgId = UUID.random19();
    }

    public Msg(String msgId) {
        this.msgId = msgId;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getMsgId() {
        return msgId;
    }
}
