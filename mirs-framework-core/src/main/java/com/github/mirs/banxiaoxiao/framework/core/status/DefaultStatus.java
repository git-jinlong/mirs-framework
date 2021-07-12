package com.github.mirs.banxiaoxiao.framework.core.status;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * A pojo bean implementation for {@link Status}.
 *
 * @author zw
 */
public class DefaultStatus implements Status, Serializable {

    private final int code;
    private final String message;

    public DefaultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static DefaultStatus toStatus(int code, String message) {
        return new DefaultStatus(code, message);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("code", code)
                .append("message", message)
                .toString();
    }
}
