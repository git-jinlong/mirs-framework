package com.github.mirs.banxiaoxiao.framework.web.rest;


import com.github.mirs.banxiaoxiao.framework.core.status.CommonStatus;
import com.github.mirs.banxiaoxiao.framework.core.status.Status;
import org.springframework.context.MessageSource;

import java.util.Collections;

/**
 * Utility class for simplify creates {@link RestResponse}.
 *
 * @author zw
 */
public final class RestResponses {

    private final MessageSource messageSource;

    public RestResponses(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Returns the {@link RestResponse} with {@link CommonStatus#ok}.
     */
    public RestResponse ok() {
        return ok(null);
    }

    public RestResponse ok(Object result) {
        return build(CommonStatus.ok, result);
    }

    public <T> RestResultResponse<T> ok2(T result) {

        return RestResponseBuilder.builder(CommonStatus.ok)
                .result(result)
                .messageSource(messageSource)
                .build3(new Object[0]);
    }

    public RestResponse okEmptyMapIfResultNull(Object result) {
        return build(CommonStatus.ok, result == null ? Collections.emptyMap() : result);
    }

    /**
     * Creates {@code RestResponse} with specified status and result.
     *
     * @param status the status
     * @param result the result
     * @return {@code RestResponse} according to status
     */
    public RestResponse build(Status status, Object result) {
        return build(status, result, new Object[0]);
    }

    /**
     * Creates {@code RestResponse} with specified status and message arguments and can contains a result.
     *
     * @param status the status
     * @param result the result
     * @param args   the args used format message
     * @return {@code RestResponse} according to status
     */
    public RestResponse build(Status status, Object result, Object[] args) {
        return RestResponseBuilder.builder(status)
                .result(result)
                .messageSource(messageSource)
                .build(args);
    }

    /**
     * Creates {@code RestResponse} with specified status and message arguments and can contains a result.
     *
     * @param status the status
     * @param result the result
     * @param args   the args used format message
     * @return {@code RestResponse} according to status
     */
    public <T> RestResultResponse<T> build3(Status status, T result, Object[] args) {
        return RestResponseBuilder.builder(status)
                .result(result)
                .messageSource(messageSource)
                .build3(args);
    }

    /**
     * Creates {@code RestResponse } with specified status and message arguments.
     *
     * @param status the status
     * @param args   the args used format message
     * @return {@code RestResponse} according to status
     */
    public RestResponse build2(Status status, Object... args) {
        return build(status, null, args);
    }

    public RestResponse error() {
        return error(CommonStatus.error);
    }

    public RestResponse error(Status status) {
        return build(status, null);
    }
}
