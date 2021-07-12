package com.github.mirs.banxiaoxiao.framework.core.util;

import com.github.mirs.banxiaoxiao.framework.common.util.UUID;

import java.io.Serializable;

public class BaseRequest implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2064757294428616432L;

    private final String requestId = UUID.random19();

    public BaseRequest() {
    }

    public String getRequestId() {
        return requestId;
    }

}
