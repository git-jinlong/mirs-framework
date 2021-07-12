package com.github.mirs.banxiaoxiao.framework.web.rest;


import com.github.mirs.banxiaoxiao.framework.core.status.Status;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

/**
 * Helper class for create {@link RestResponse}.
 *
 * @author zw
 */
public final class RestResponseBuilder {

    private int code;
    private String message;
    private Object result = null;
    private MessageSource messageSource;

    private RestResponseBuilder() {
    }

    public RestResponseBuilder code(int code) {
        this.code = code;
        return this;
    }

    public RestResponseBuilder message(String message) {
        this.message = message;
        return this;
    }

    public RestResponseBuilder result(Object result) {
        this.result = result;
        return this;
    }

    public RestResponseBuilder messageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
        return this;
    }

    public RestResponse build() {
        return build(new Object[0]);
    }

    @SuppressWarnings("unchecked")
    public RestResponse build(Object[] args) {
        if (messageSource != null && StringUtils.isNotBlank(message)) {
            message = messageSource.getMessage(message, args, message, null);
        }
        if (result == null) {
            return new RestResponse(code, message);
        }
        RestResultResponse resp = new RestResultResponse(code, message);
        resp.setResult(result);
        return resp;
    }

    public RestResultResponse build3(Object[] args) {
        if (messageSource != null && StringUtils.isNotBlank(message)) {
            message = messageSource.getMessage(message, args, message, null);
        }
        if (result == null) {
            return new RestResultResponse(code, message);
        }
        RestResultResponse resp = new RestResultResponse(code, message);
        resp.setResult(result);
        return resp;
    }

    public static RestResponseBuilder builder() {
        return new RestResponseBuilder();
    }

    /**
     * Creates a newly builder and set code and message from status.
     *
     * @param status the response status
     * @return a newly builder contains code and message from status
     */
    public static RestResponseBuilder builder(Status status) {
        return new RestResponseBuilder()
                .code(status.getCode())
                .message(status.getMessage());
    }


}
